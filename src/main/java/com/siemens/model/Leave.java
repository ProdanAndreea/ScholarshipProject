package com.siemens.model;


import lombok.Setter;

import java.time.LocalDate;

import java.time.LocalTime;


//@Builder
@Setter
public class Leave {
    private LocalDate leaveDate;
    private LocalTime numberOfHours;
    private LocalTime hoursToCover;
    //in case the need to keep track of missing interval time appears
    private LocalTime startHour;
    private LocalTime endHour;

    public Leave(LocalDate leaveDate, LocalTime numberOfHours) {
        this.leaveDate = leaveDate;
        this.numberOfHours = numberOfHours;
        this.hoursToCover = numberOfHours;

    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public LocalTime getNumberOfHours() {
        return numberOfHours;
    }

    public LocalTime getHoursToCover() {
        return hoursToCover;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setCoveredHours(LocalTime coveredHours) {
        hoursToCover = hoursToCover.minusMinutes(coveredHours.getMinute());
        hoursToCover = hoursToCover.minusHours(coveredHours.getHour());
    }

    public void deleteFromCoveredHours(LocalTime coveredHours) {
        hoursToCover = hoursToCover.plusMinutes(coveredHours.getMinute());
        hoursToCover = hoursToCover.plusHours(coveredHours.getHour());
    }

    @Override
    public String toString() {
        return "Leave{" +
                "leaveDate=" + leaveDate +
                ", numberOfHours=" + numberOfHours +
                ", hoursToCover=" + hoursToCover +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                '}';
    }
}
