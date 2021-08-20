package com.putchar.securityclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.putchar.securityclient.model.RegistrationUser;
import com.putchar.securityclient.model.ResponseToken;
import com.putchar.securityclient.model.User;

@RestController
public class TestController 
{
	private static final String REGISTRATION_URL = "http://localhost:8080/register";
	private static final String AUTHENTICATION_URL = "http://localhost:8080/authenticate";
	private static final String GREET_URL = "http://localhost:8080/landing_api/greet";
	
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping(value="/getResponse", method = RequestMethod.GET)
	public String getResponse() throws JsonProcessingException
	{
		String response = null;
		
		try 
		{
			RegistrationUser regUser = getRegistrationUser();
			String regBody = getBody(regUser);
			HttpHeaders regHeaders = getHeaders();
			HttpEntity<String> regEntity = new HttpEntity<String>(regBody, regHeaders);
			
			ResponseEntity<String> registartionResponse = restTemplate.exchange(REGISTRATION_URL, HttpMethod.POST, 
					regEntity, String.class);
			
			if(registartionResponse.getStatusCode().equals(HttpStatus.OK)) 
			{
				User authenticationUser = getAuthenticationUser();
				String authenticationBody = getBody(authenticationUser);
				HttpHeaders authenticationHeaders = getHeaders();
				HttpEntity<String> authenticationEntity = new HttpEntity<String>(authenticationBody, authenticationHeaders);
				
				ResponseEntity<ResponseToken> authenticationResponse = restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST, 
						authenticationEntity, ResponseToken.class);
				
				if(authenticationResponse.getStatusCode().equals(HttpStatus.OK)) 
				{
					String token = "Bearer "+authenticationResponse.getBody().getJwttoken();
					System.out.println(token);
					HttpHeaders headers = new HttpHeaders();
					headers.set("Authorization", token);
					HttpEntity<String> greetEntity = new HttpEntity<String>(headers);
					
					ResponseEntity<String> greetResponse = restTemplate.exchange(GREET_URL, HttpMethod.GET, greetEntity, String.class);
					if(greetResponse.getStatusCode().equals(HttpStatus.OK)) 
					{
						response = greetResponse.getBody();
					}
				}
				
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{}
		
		return response;
	}
	
	private HttpHeaders getHeaders() 
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	private String getBody(final User user) throws JsonProcessingException 
	{
		return new ObjectMapper().writeValueAsString(user);
	}

	private RegistrationUser getRegistrationUser() 
	{
		RegistrationUser regUser = new RegistrationUser();
		regUser.setUsername("givantha");
		regUser.setPassword("password");
		regUser.setRole("ROLE_ADMIN");
		return regUser;
	}
	
	private User getAuthenticationUser() 
	{
		User user = new User();
		user.setUsername("givantha");
		user.setPassword("password");
		return user;
	}

}
