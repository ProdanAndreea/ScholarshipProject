package com.siemens.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;

//@Builder
public class Recovery {
    private Date leaveDate;
    private Date recoveryDate;
    private Integer numberOfHours;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Recovery(Date leaveDate, Date recoveryDate, Integer numberOfHours, LocalDateTime startTime) {
        this.leaveDate = leaveDate;
        this.recoveryDate = recoveryDate;
        this.numberOfHours = numberOfHours;
        this.startTime = startTime;
        endTime = startTime.plusHours(numberOfHours);
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public Date getRecoveryDate() {
        return recoveryDate;
    }

    public Integer getNumberOfHours() {
        return numberOfHours;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }


}
