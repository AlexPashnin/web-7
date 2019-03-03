package ru.ifmo.web.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
public class AstartesRequestObject {
    private Long id;
    private String name;
    private String title;
    private String position;
    private String planet;
    private Date birthdate;
}
