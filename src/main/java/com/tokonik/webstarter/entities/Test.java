package com.tokonik.webstarter.entities;

import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


/**
 * The persistent class for the tests database table.
 * 
 */
@Entity
@Table(name="tests")
//@NamedQuery(name="test.findAll", query="SELECT a FROM test a")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Test implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="test_id")
	private Integer testId;

	@Column(name="test_description")
	private String testDescription;

	@Column(name="test_name")
	private String testName;

	@Temporal(TemporalType.DATE)
	@Column(name="test_date")
	private Date testDate;




}