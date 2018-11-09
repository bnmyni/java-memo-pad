package com.aspire.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Person implements Serializable {
    private static final long serialVersionUID = -2830939627085135084L;

    @Id
	@GeneratedValue
	private Long id;

	private String name;

	private Integer age;

	private String address;

}
