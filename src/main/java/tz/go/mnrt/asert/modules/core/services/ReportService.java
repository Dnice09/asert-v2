package tz.go.mnrt.asert.modules.core.services;

import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.core.dtos.ReportDto;
import tz.go.mnrt.asert.modules.core.dtos.JasperReportDto;
import tz.go.mnrt.asert.modules.core.dtos.AsertReportDto;
import tz.go.mnrt.asert.modules.core.dtos.ReportTreeDto;
import tz.go.mnrt.asert.modules.core.entities.Report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ReportService {
    Page<AsertReportDto> findAll(Pageable page, Map<String, String> search);

    AsertReportDto create(AsertReportDto jasperReportDto);

    Boolean delete(UUID uuid);

    AsertReportDto update(UUID uuid, AsertReportDto jasperReportDto);

    Set<Object> queryReportParams(String jrxmlFileName) throws JRException, IOException;

    String generateReport(JasperReportDto asertJasperDto) throws JRException, IOException, SQLException;

    ReportTreeDto tree(Long parentId);

    ReportTreeDto tree(Long parentId, String searchTerm);

    List<Report> getReportTree();

    List<ReportDto> children(Long parentId);
}
