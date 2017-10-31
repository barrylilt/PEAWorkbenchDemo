package com.saama.workbench.model;

import java.util.Base64;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.saama.workbench.util.AppConstants;
import com.saama.workbench.util.PEAUtils;
import com.saama.workbench.util.PropertiesUtil;

@Entity
@Table(name="WB_USER_PROFILE"
    ,schema="LND"
    
)

public class UserProfile implements java.io.Serializable{
	
	private long userId;
	private String company;
	private String userName;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String country;
	private String postalCode;
	private String aboutMe;
	private byte[] photo;
	
	private String password;
	private String isActive;
	private List<AuthRole> role;
	private String theme;
	private String dateFormat;
	private String language;
	
	private String dateFormatUI;
	private String dateFormatBK;
	
	private String base64photo;
	
	
	public UserProfile() {
		
	}


	public UserProfile(long userId, String company, String userName,
			String emailAddress, String firstName, String lastName,
			String address, String city, String country, String postalCode,
			String aboutMe, byte[] photo) {
		this.userId = userId;
		this.company = company;
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.country = country;
		this.postalCode = postalCode;
		this.aboutMe = aboutMe;
		this.photo = photo;
	}
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO)
	@Column(name="UserId", unique= true, nullable= false, insertable=false, updatable=false)
	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Column(name="Company")
	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}

	@Column(name="UserName", nullable=false)
	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name="EmailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name="FirstName")
	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name="LastName")
	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name="Address")
	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="City")
	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}

	@Column(name="Country")
	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name="PostalCode")
	public String getPostalCode() {
		return postalCode;
	}


	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Column(name="AboutMe")
	public String getAboutMe() {
		return aboutMe;
	}


	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	@Column(name="Photo")
	public byte[] getPhoto() {
		return photo;
	}


	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	@Column(name="password")
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name="isActive")
	public String getIsActive() {
		return isActive;
	}


	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}


	@Transient
	public String getBase64photo() {
		
		return PEAUtils.convertToBase64photo(this.photo);
		
//    	if (this.photo != null) {
//			byte[] encoded = Base64.getEncoder().encode(this.photo);
//			if(encoded!=null)
//				return "data:image/jpeg;base64," + new String(encoded);
//    	}
//		return "";
	}


	public void setBase64photo(String base64photo) {
		this.base64photo = base64photo;
	}

	@OneToMany(cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	@JoinTable(schema = "LND", name = "WB_USER_ROLE", joinColumns = { @JoinColumn(name = "UserId") }, inverseJoinColumns = { @JoinColumn(name = "UserID")})
	public List<AuthRole> getRole() {
		return role;
	}


	public void setRole(List<AuthRole> role) {
		this.role = role;
	}


	@Column(name="Theme")
	public String getTheme() {
		return theme;
	}


	public void setTheme(String theme) {
		this.theme = theme;
	}


	@Column(name="DateFormat")	
	public String getDateFormat() {
		return dateFormat;
	}


	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	@Column(name="Language")
	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	@Transient
	public String getDateFormatUI() {
		if (this.dateFormatUI != null) 
			return this.dateFormatUI;
		else if (dateFormat != null) {
			String[] arrDf = dateFormat.split(PropertiesUtil.getProperty(AppConstants.COMMON_SETTINGS_SPLITTER_DATE_FORMAT));
			if (arrDf.length > 1)
				return arrDf[1];
		}
		return null;
	}

	public void setDateFormatUI(String dateFormatUI) {
		this.dateFormatUI = dateFormatUI;
	}


	@Transient
	public String getDateFormatBK() {
		if (this.dateFormatBK != null) 
			return this.dateFormatBK;
		else if (dateFormat != null) {
			String[] arrDf = dateFormat.split(PropertiesUtil.getProperty(AppConstants.COMMON_SETTINGS_SPLITTER_DATE_FORMAT));
			if (arrDf.length > 0)
				return arrDf[0];
		}
		return null;
	}

	public void setDateFormatBK(String dateFormatBK) {
		this.dateFormatBK = dateFormatBK;
	}
	
	
	
/*	public void setRole(AuthRole role) {
		this.role = role;
	}
	
	
	@OneToMany(cascade = CascadeType.PERSIST, fetch=FetchType.EAGER)
	@JoinTable(schema = "LND", name = "WB_USER_ROLE", joinColumns = { @JoinColumn(name = "UserId") }, inverseJoinColumns = { @JoinColumn(name = "UserID")})
	public AuthRole getRole() {
		return role;
	}
*/
}
