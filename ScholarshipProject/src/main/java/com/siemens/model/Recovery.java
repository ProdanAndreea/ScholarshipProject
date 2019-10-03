package com.siemens.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//@Builder
public class Recovery {
    private LocalDate leaveDate;
    private LocalDate recoveryDate;
    private LocalTime numberOfHours;
    //if the need to keep track of recovery time interval
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Recovery(LocalDate leaveDate, LocalDate recoveryDate, LocalTime numberOfHours) {
        this.leaveDate = leaveDate;
        this.recoveryDate = recoveryDate;
        this.numberOfHours = numberOfHours;

    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public LocalDate getRecoveryDate() {
        return recoveryDate;
    }

    public LocalTime getNumberOfHours() {
        return numberOfHours;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }


}
