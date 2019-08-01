package com.siemens.model;

import lombok.*;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Request {
    private String fileName;
    private String emailSender;
    private File file;
    private boolean isSigned;
    private boolean isSent;
    @Override
    public String toString(){
        String toString = (isSigned) ? this.fileName + " - SEMNAT" : this.fileName;
        return (isSent) ? toString + " - RASPUNS TRIMIS" : toString;
    }
}
