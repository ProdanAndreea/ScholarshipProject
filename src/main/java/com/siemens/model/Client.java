package com.siemens.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + "]";
    }
}
