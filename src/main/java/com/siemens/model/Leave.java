package com.siemens.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;

//@Builder
public class Leave {
    private Date leaveDate;
    private Integer numberOfHours;
    private Integer coveredHours;
    private LocalDateTime startHour;
    private LocalDateTime endHour;

    public Leave(Date leaveDate, Integer numberOfHours, LocalDateTime startHour) {
        this.leaveDate = leaveDate;
        this.numberOfHours = numberOfHours;
        this.startHour = startHour;
        this.coveredHours = 0;
        endHour = startHour.plusHours(numberOfHours);
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public Integer getNumberOfHours() {
        return numberOfHours;
    }

    public Integer getCoveredHours() {
        return coveredHours;
    }

    public LocalDateTime getStartHour() {
        return startHour;
    }

    public LocalDateTime getEndHour() {
        return endHour;
    }

    public void setCoveredHours(Integer coveredHours) {
        this.coveredHours = coveredHours;
    }

    public void setEndHour(LocalDateTime endHour) {
        this.endHour = endHour;
    }
}
