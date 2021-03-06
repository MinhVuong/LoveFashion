package com.example.entity;
// Generated Dec 19, 2015 11:20:12 PM by Hibernate Tools 4.3.1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * EavAttributeOption generated by hbm2java
 */
@Entity
@Table(name = "eav_attribute_option", catalog = "lovefashion")
public class EavAttributeOption implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer optionId;
	private EavAttribute eavAttribute;
	private Set<EavAttributeOptionValue> eavAttributeOptionValues = new HashSet<EavAttributeOptionValue>(0);

	public EavAttributeOption() {
	}

	public EavAttributeOption(EavAttribute eavAttribute) {
		this.eavAttribute = eavAttribute;
	}

	public EavAttributeOption(EavAttribute eavAttribute, Set<EavAttributeOptionValue> eavAttributeOptionValues) {
		this.eavAttribute = eavAttribute;
		this.eavAttributeOptionValues = eavAttributeOptionValues;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "option_id", unique = true, nullable = false)
	public Integer getOptionId() {
		return this.optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_id", nullable = false)
	public EavAttribute getEavAttribute() {
		return this.eavAttribute;
	}

	public void setEavAttribute(EavAttribute eavAttribute) {
		this.eavAttribute = eavAttribute;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eavAttributeOption")
	public Set<EavAttributeOptionValue> getEavAttributeOptionValues() {
		return this.eavAttributeOptionValues;
	}

	public void setEavAttributeOptionValues(Set<EavAttributeOptionValue> eavAttributeOptionValues) {
		this.eavAttributeOptionValues = eavAttributeOptionValues;
	}

}
