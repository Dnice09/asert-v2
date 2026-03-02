package tz.go.mnrt.asert.modules.core.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.core.services.ReportService;
import tz.go.mnrt.asert.modules.core.dtos.ReportDto;
import tz.go.mnrt.asert.modules.core.dtos.JasperReportDto;
import tz.go.mnrt.asert.modules.core.dtos.AsertReportDto;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;
import tz.go.mnrt.asert.modules.form.formsubmission.services.FormSubmissionService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;

@RestController
@RequestMapping(Constant.API_V1 + "/asert-reports")
@RequiredArgsConstructor
@Slf4j
public class JasperReportResource {

    private final ReportService jasperReportService;
    private final HotelService hotelService;
    private final FormSubmissionService formSubmissionService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(jasperReportService.findAll(PageRequest.of(pagination.getPageNumber(),
                pagination.getPageSize(), pagination.getSortOr(Sort.by("id").descending())), search));
    }

    @GetMapping("/children")
    public CustomApiResponse children(@RequestParam(name = "parentId", defaultValue = "0") Long parentId) {
        List<ReportDto> jasperReports = jasperReportService.children(parentId);
        return CustomApiResponse.ok(jasperReports);
    }

    @PostMapping
    public CustomApiResponse create(@Valid @RequestBody AsertReportDto jasperReportDto) {
        if (jasperReportDto.getId() != null || jasperReportDto.getUuid() != null) {
            throw new ValidationException("Jasper report can not contain an id or a uuid");
        }
        jasperReportService.create(jasperReportDto);
        return CustomApiResponse.ok("Jasper report created successfully");
    }

    @PutMapping("/{uuid}")
    public CustomApiResponse update(@Valid @RequestBody AsertReportDto jasperReportDto, @PathVariable UUID uuid) {
        if (jasperReportDto.getUuid() == null || !Objects.equals(jasperReportDto.getUuid(), uuid)) {
            throw new ValidationException("Jasper report uuid must be present and equals to path id {" + uuid + "}");
        }
        jasperReportService.update(uuid, jasperReportDto);
        return CustomApiResponse.ok("Department updated successfully");
    }

    @GetMapping("/params/{jrxmlFileName}")
    public CustomApiResponse getJrxmlParams(@PathVariable String jrxmlFileName) throws JRException, IOException {
        var result = jasperReportService.queryReportParams(jrxmlFileName);
        return CustomApiResponse.ok(result);
    }

    @PostMapping("/generate")
    public CustomApiResponse generateReport(@RequestBody JasperReportDto asertJasperDto)
            throws JRException, IOException, SQLException {
        var resource = jasperReportService.generateReport(asertJasperDto);
        return CustomApiResponse.ok("Success", resource);
    }

    @PostMapping("/generate-hotel-rating-report")
    @NoAuthorization
    public CustomApiResponse generateHotelRatingReport(@RequestBody Map<String, String> request)
            throws JRException, IOException, SQLException {
        String hotelUuidStr = request.get("hotelUuid");
        if (hotelUuidStr == null) {
            throw new ValidationException("hotelUuid is required");
        }
        UUID hotelUuid = UUID.fromString(hotelUuidStr);

        var hotel = hotelService.findByUuid(hotelUuid);
        if (hotel == null) {
            throw new ValidationException("Hotel not found");
        }

        var assessmentResult = formSubmissionService.getHotelAssessmentResult(hotelUuid);

        // Extract numeric rating from "5 Star" string
        int starRating = 0;
        if (assessmentResult.getStarRating() != null) {
            String numericPart = assessmentResult.getStarRating().replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) {
                starRating = Integer.parseInt(numericPart);
            }
        }

        // Prepare Visual Data
        String stars = generateStars(starRating);
        String classification = getClassificationFromStars(starRating);
        String eacLogo = loadImageAsBase64("images/ealogo.png");
        String coatOfArms = loadImageAsBase64("images/coat_of_arms.png");
        String secretarySignature = loadImageAsBase64("images/secretary-signature.png");
        String authoritySignature = loadImageAsBase64("images/dt-signature.png");

        // Format current date for the JRXML
        String issueDate = java.time.LocalDate.now().toString(); // format: yyyy-MM-dd

        // Generate unique serial number based on hotel UUID and timestamp
        // Format: YYYYMMDD followed by last 6 chars of hotel UUID (numbers only)
        String datePrefix = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidSuffix = hotelUuid.toString().replaceAll("[^0-9]", "").substring(0,
                Math.min(6, hotelUuid.toString().replaceAll("[^0-9]", "").length()));
        String serialNo = datePrefix + uuidSuffix;

        JasperReportDto dto = new JasperReportDto();
        dto.setName("hotel-rating-certificate");

        Map<String, String> params = new HashMap<>();
        // Ensure these keys match the JRXML exactly
        params.put("facilityName", hotel.getName());
        params.put("grade", classification);
        params.put("stars", stars);

        // Naming this 'issue_date' ensures your Service casts it to a Date object
        // correctly (ends with 'ate' triggers date parsing in ReportServiceImpl)
        params.put("issue_date", issueDate);
        params.put("SerialNo", serialNo);
        params.put("hotelUuid", hotelUuidStr);

        // Log params BEFORE adding base64 images to avoid logging huge strings
        log.info("Generating hotel rating report with params (before images): {}", params);

        // Add images after logging
        params.put("eacLogo", eacLogo);
        params.put("coatOfArms", coatOfArms);
        params.put("permanentSecretarySignature", secretarySignature);
        params.put("issuingAuthoritySignature", authoritySignature);

        dto.setParams(params);

        var resource = jasperReportService.generateReport(dto);
        return CustomApiResponse.ok("Success", resource);
    }

    private String generateStars(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            sb.append("★");
        }
        return sb.toString();
    }

    private String getClassificationFromStars(int stars) {
        switch (stars) {
            case 1:
                return "ONE STAR";
            case 2:
                return "TWO STAR";
            case 3:
                return "THREE STAR";
            case 4:
                return "FOUR STAR";
            case 5:
                return "FIVE STAR";
            default:
                return "UNCLASSIFIED";
        }
    }

    private String loadImageAsBase64(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                log.error("Image resource does not exist: {}", path);
                return "";
            }

            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            log.info("Successfully loaded image: {} ({} bytes)", path, bytes.length);

            // Detect image type from path
            String mimeType = "image/png";
            if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (path.toLowerCase().endsWith(".gif")) {
                mimeType = "image/gif";
            } else if (path.toLowerCase().endsWith(".svg")) {
                mimeType = "image/svg+xml";
            }

            return "data:" + mimeType + ";base64," + base64;
        } catch (IOException e) {
            log.error("Failed to load image from path: {}. Error: {}", path, e.getMessage(), e);
            return "";
        }
    }

    @GetMapping("/tree")
    public CustomApiResponse tree(@RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
            @RequestParam(value = "parentId", defaultValue = "0") Long parentId) {
        if (searchTerm != null) {
            return CustomApiResponse.ok(jasperReportService.tree(parentId, searchTerm));
        } else {
            return CustomApiResponse.ok(jasperReportService.getReportTree());
        }
    }
}
