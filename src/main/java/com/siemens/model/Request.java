package com.siemens.model;

import lombok.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Request implements Comparable<Request> {
    private String fileName;
    private String emailSender;
    private File file;
    private boolean isSigned;
    private LocalDate sendDate;
    private Date sentTime;
    @Override
    public String toString(){
        return (isSigned) ? this.fileName + " - SEMNAT" : this.fileName;
    }

    @Override
    public int compareTo(Request o)
    {
        if (this.isSigned && !o.isSigned) {
           return 1;
        }

        if (!this.isSigned && o.isSigned) {
            return -1;
        }

        int dateComparison = this.sendDate.compareTo( o.getSendDate());
        if (dateComparison == 0) {
            // compare hour:minute
            return (this.sentTime.after(o.getSentTime()) ? 1 : -1);
        }

        return dateComparison;
    }

    public Request clone() {
        return Request.builder()
                .fileName(this.fileName)
                .emailSender(this.emailSender)
                .file(this.file)
                .isSigned(this.isSigned)
                .sendDate(this.sendDate)
                .build();
    }
}
