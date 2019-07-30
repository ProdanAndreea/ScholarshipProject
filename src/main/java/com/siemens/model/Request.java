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
    @Override
    public String toString(){
        return (isSigned) ? this.fileName + " - SEMNAT" : this.fileName;
    }
}
