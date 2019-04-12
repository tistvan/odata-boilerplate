package com.penninkhof.odata.entities;

import com.penninkhof.odata.annotations.SAPLineItem;
import com.penninkhof.odata.annotations.Sap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Member2 {

	@Id
	private int id;
	@SAPLineItem
    private String firstName;
	@Sap(filterable=true, sortable=true)
	@SAPLineItem
    private String lastName;

	@Sap(filterable=true, sortable=true)
	@SAPLineItem
	private String asdf;

    public Member2() {}

	public Member2(int id, String firstName, String lastName, String asdf) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.asdf = asdf;
	}

	@Override
    public String toString() {
        return String.format(
                "Member2[id=%d, firstName='%s', lastName='%s', asdf='%s']",
                id, firstName, lastName, asdf);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAsdf() {
		return asdf;
	}

	public void setAsdf(String asdf) {
		this.asdf = asdf;
	}
}