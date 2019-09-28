package com.product.content.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project {
	private static final String DATE_FORMAT = "MMM, YYYY";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	private String productName;
	@Column(columnDefinition = "TEXT")
	private String description;
	private Date fromDate;
	private boolean isNewest;
	private Date toDate;
	private String location;
	private String customerName;
	private String thumbnail;
	private String category;
	@ElementCollection
	private List<String> images;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	public String getFormattedFromDate(){
		return dateFormat.format(this.fromDate);
	};


    public String getFormattedToDate(){
		return dateFormat.format(this.toDate);
	}

}
