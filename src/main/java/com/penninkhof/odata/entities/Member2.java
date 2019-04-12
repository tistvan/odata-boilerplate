package com.penninkhof.odata.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member2 {

	@Id
	private int id;
    private String firstName;
    private String lastName;

	private String asdf;
	private String asdf2;
	private String asdf3;

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

	public String getAsdf2() {
		return asdf2;
	}

	public void setAsdf2(String asdf2) {
		this.asdf2 = asdf2;
	}

	public String getAsdf3() {
		return asdf3;
	}

	public void setAsdf3(String asdf3) {
		this.asdf3 = asdf3;
	}
}
