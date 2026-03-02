package tz.go.mnrt.asert.modules.core.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.core.dtos.ReportDto;
import tz.go.mnrt.asert.modules.core.entities.Report;
import tz.go.mnrt.asert.modules.core.repositories.ReportRepository;
import tz.go.mnrt.asert.modules.core.dtos.JasperReportDto;
import tz.go.mnrt.asert.modules.core.dtos.AsertReportDto;
import tz.go.mnrt.asert.modules.core.dtos.ReportTreeDto;
import tz.go.mnrt.asert.modules.core.services.ReportService;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.utils.Jasper;

import javax.sql.DataSource;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "jasperReportService")
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl extends SimpleSearchService<Report> implements ReportService {
    private final DataSource dataSource;
    private final Jasper jasper;
    @Value("${asert.jasper-reports.templates-directory}")
    String jasperTemplateDirectory;

    @Value("${asert.jasper-reports.compiled-directory}")
    String jasperOutputDirectory;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Page<AsertReportDto> findAll(Pageable page, Map<String, String> search) {
        return reportRepository.findAll(createSpecification(Report.class, search), page)
                .map(AsertReportDto::new);
    }

    @Override
    public AsertReportDto create(AsertReportDto jasperReportDto) {
        Report jasperReport = new Report();
        jasperReport.setName(jasperReportDto.getName());
        jasperReport.setUrl(jasperReportDto.getUrl());

        // Set parent_id
        Report parent = null;
        if (jasperReportDto.getParentId() != null) {
            Optional<Report> fisJasperReportOptional = reportRepository
                    .findById(jasperReportDto.getParentId());
            if (fisJasperReportOptional.isPresent()) {
                parent = fisJasperReportOptional.get();
            }
        }

        jasperReport.setParent(parent);
        Report result = reportRepository.save(jasperReport);
        jasperReportDto.setId(result.getId());
        return jasperReportDto;
    }

    @Override
    public Boolean delete(UUID uuid) {
        Optional<Report> jasperReportOptional = reportRepository.findByUuid(uuid);
        if (jasperReportOptional.isPresent()) {
            reportRepository.deleteByUuid(uuid);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AsertReportDto update(UUID uuid, AsertReportDto jasperReportDto) {
        Optional<Report> jasperReportOptional = reportRepository.findByUuid(uuid);
        if (jasperReportOptional.isPresent()) {
            Report existing = jasperReportOptional.get();
            existing.setUrl(jasperReportDto.getUrl());
            existing.setName(jasperReportDto.getName());

            // Get the parent report
            if (jasperReportDto.getParentId() != null) {
                Optional<Report> parentOptional = reportRepository.findById(jasperReportDto.getParentId());
                if (parentOptional.isPresent()) {
                    Report parent = parentOptional.get();
                    existing.setParent(parent);
                }
            }

            Report report = reportRepository.save(existing);
            jasperReportDto.setId(report.getId());
        } else {
            throw new ValidationException("Report with id " + uuid + " has not been found");
        }
        return jasperReportDto;
    }

    @Override
    public Set<Object> queryReportParams(String jrxmlFileName) throws JRException, IOException {
        this.compileJrxml(jrxmlFileName);
        JasperReport jasperReport = (JasperReport) JRLoader
                .loadObject(new File(jasperTemplateDirectory + "/" + jrxmlFileName + ".jasper"));
        JRParameter[] params = jasperReport.getParameters();

        for (JRParameter param : params) {
            if (!param.isSystemDefined() && param.isForPrompting()) {
                param.getName();
                param.getDescription();
                param.getDefaultValueExpression();
                param.getNestedTypeName();
            }
        }

        Set<Object> userDefinedParams = new HashSet<>();
        List<JRParameter> paramsList = Arrays.asList(params);
        paramsList.forEach(jrParameter -> {
            if (!jrParameter.isSystemDefined()) {
                Map<String, Object> jsonParams = new HashMap<>();
                if (!jrParameter.getName().equals("logo")) {
                    jsonParams.put("name", jrParameter.getName());
                    jsonParams.put("type", jrParameter.getValueClass().getSimpleName());
                    userDefinedParams.add(jsonParams);
                }
            }
        });

        return userDefinedParams;
    }

    @Override
    public String generateReport(JasperReportDto fisJasperDto) throws JRException, IOException, SQLException {
        Map<String, String> params = fisJasperDto.getParams();
        Set<String> jasperParams = params.keySet();

        String reportName = fisJasperDto.getName();
        Map<String, Object> parameters = new HashMap<>();

        log.info("Processing {} parameters for report '{}'", jasperParams.size(), reportName);

        jasperParams.forEach(p -> {
            String paramValue = params.get(p);
            String last_characters = p.length() >= 3 ? p.substring(p.length() - 3) : "";

            // If the last characters end with _id, then cast to Long
            if (last_characters.equals("_id")) {
                Long longValue = Long.valueOf(paramValue);
                parameters.put(p, longValue);
                log.debug("Parameter '{}': {} (converted to Long)", p, longValue);
            }
            // If the last characters end with 'ate', parse as Date
            else if (last_characters.equals("ate")) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(paramValue);
                    parameters.put(p, date);
                    log.debug("Parameter '{}': {} (converted to Date)", p, date);
                } catch (ParseException e) {
                    log.error("Failed to parse date parameter '{}' with value '{}'", p, paramValue, e);
                    throw new RuntimeException("Failed to parse date parameter: " + p, e);
                }
            }
            // Otherwise keep as String
            else {
                parameters.put(p, paramValue);
                // Don't log full base64 strings
                if (paramValue != null && paramValue.startsWith("data:image")) {
                    log.debug("Parameter '{}': base64 image [{} chars]", p, paramValue.length());
                } else {
                    log.debug("Parameter '{}': {} (String)", p, paramValue);
                }
            }
        });

        log.info("Final parameters being passed to JasperReports: {}", parameters.keySet());

        Connection connection = dataSource.getConnection();
        ClassPathResource classPathResource = new ClassPathResource(
                "jasper-templates/reports/" + reportName + ".jrxml");
        InputStream resourceAsStream = classPathResource.getInputStream();

        // Disable XML validation to avoid schema loading issues
        System.setProperty("net.sf.jasperreports.xml.validation", "false");

        log.info("Compiling report template: {}", reportName);
        JasperReport jasperReport = JasperCompileManager.compileReport(resourceAsStream);
        JRSaver.saveObject(jasperReport, jasperTemplateDirectory + "/" + reportName + ".jasper");

        log.info("Filling report with parameters...");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperTemplateDirectory + "/" + reportName + ".jasper",
                parameters, connection);

        log.info("Report generated successfully. Pages: {}", jasperPrint.getPages().size());

        String base64 = jasper.exportBase64(jasperPrint);
        connection.close();
        return base64;
    }

    @Override
    public ReportTreeDto tree(Long parentId, String searchTerm) {
        if (parentId == null) {
            Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
            Page<Report> top = reportRepository.findAllByParentIsNull(searchTerm.toLowerCase(), pageable);
            if (top.hasContent()) {
                Report topLevel = top.getContent().get(0);
                return getJasperReportTreeDto(topLevel, searchTerm.toLowerCase());
            } else {
                return null;
            }
        } else {
            Optional<Report> top = reportRepository.findById(parentId);
            if (top.isPresent()) {
                Report topLevel = top.get();
                return getJasperReportTreeDto(topLevel, searchTerm.toLowerCase());
            } else {
                return null;
            }
        }
    }

    @Override
    public ReportTreeDto tree(Long parentId) {
        if (parentId == null) {
            Pageable pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
            Page<Report> top = reportRepository.findAllByParentIsNull(pageable);
            if (top.hasContent()) {
                Report topLevel = top.getContent().get(0);
                return getJasperReportTreeDto(topLevel);
            } else {
                return null;
            }
        } else {
            Optional<Report> top = reportRepository.findById(parentId);
            if (top.isPresent()) {
                Report topLevel = top.get();
                return getJasperReportTreeDto(topLevel);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Report> getReportTree() {
        return reportRepository.findAllByParentIsNull();
    }

    @NotNull
    private ReportTreeDto getJasperReportTreeDto(Report topLevel, String query) {
        ReportTreeDto currentLevel = new ReportTreeDto();
        currentLevel.setId(topLevel.getId());
        currentLevel.setName(topLevel.getName());
        currentLevel.setUrl(topLevel.getUrl());
        List<ReportTreeDto> children = new ArrayList<>();
        List<Report> immediateChildren = reportRepository.findAllByParentId(topLevel.getId(),
                query.toLowerCase());
        for (Report child : immediateChildren) {
            ReportTreeDto rg = new ReportTreeDto();
            rg.setId(child.getId());
            rg.setName(child.getName());
            rg.setUrl(child.getUrl());
            children.add(rg);
        }
        currentLevel.setChildren(children);
        return currentLevel;
    }

    @NotNull
    private ReportTreeDto getJasperReportTreeDto(Report topLevel) {
        ReportTreeDto currentLevel = new ReportTreeDto();
        currentLevel.setId(topLevel.getId());
        currentLevel.setName(topLevel.getName());
        currentLevel.setUrl(topLevel.getUrl());
        List<ReportTreeDto> children = new ArrayList<>();
        List<Report> immediateChildren = reportRepository.findAllByParentId(topLevel.getId());
        for (Report child : immediateChildren) {
            ReportTreeDto rg = new ReportTreeDto();
            rg.setId(child.getId());
            rg.setUrl(child.getUrl());
            rg.setName(child.getName());
            rg.setParentId(child.getParent().getId());
            // rg.setIsDealerReport(child.getIsDealerReport());
            children.add(rg);
        }
        currentLevel.setChildren(children);
        return currentLevel;
    }

    private void compileJrxml(String jrxmlFileName) throws JRException, IOException {
        /**
         * Compile/get compiles file
         */
        ClassPathResource classPathResource = new ClassPathResource(
                "jasper-templates/reports/" + jrxmlFileName + ".jrxml");
        InputStream resourceAsStream = classPathResource.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(resourceAsStream);
        JRSaver.saveObject(jasperReport, jasperTemplateDirectory + "/" + jrxmlFileName + ".jasper");
    }

    @Override
    public List<ReportDto> children(Long parentId) {
        if (parentId == null || parentId == 0) {
            return reportRepository.findReportByParentIsNull().stream().map(r -> {
                ReportDto jasperReportDto = new ReportDto();
                jasperReportDto.setId(r.getId());
                jasperReportDto.setName(r.getName());
                jasperReportDto.setUrl(r.getUrl());
                jasperReportDto.setUuid(r.getUuid());
                return jasperReportDto;
            }).collect(Collectors.toList());
        } else {
            return reportRepository.children(parentId);
        }
    }
}
