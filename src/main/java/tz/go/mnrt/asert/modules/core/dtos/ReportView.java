package tz.go.mnrt.asert.modules.core.dtos;

import java.util.Set;

import tz.go.mnrt.asert.modules.core.entities.Report;

public interface ReportView {

    Long getId();

    String getName();

    String getUrl();

    Report getParent();

    Set<ReportViewChild> getChildren();

    interface ReportViewChild {
        Long getId();

        String getName();

        String getUrl();

        Report getParent();
    }
}
