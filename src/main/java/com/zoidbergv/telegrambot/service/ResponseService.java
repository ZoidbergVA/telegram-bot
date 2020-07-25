package com.zoidbergv.telegrambot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.zoidbergv.telegrambot.model.Response;

@Service
public class ResponseService {

	private List<Response> responses;
	
	@PostConstruct
	public void init() {
		this.responses = new ArrayList<Response>();
		this.responses.add(new Response(1, "Lei non si e mai comportato in quel modo o solo molto raramente (dallo 0 al 10% delle volte)."));
		this.responses.add(new Response(2, "Lei si comporta in quel modo raramente (dal 10 al 50% delle volte)."));
		this.responses.add(new Response(3, "Lei si comporta in quel modo spesso (dal 50 al 90% delle volte)."));
		this.responses.add(new Response(4, "Lei si comporta in quel modo sempre o quasi sempre (90-100% delle volte)."));
	}
	
	public List<Response> getResponses() {
		return this.responses;
	}
	
	public Optional<Response> getReponseByText(String text) {
		for (Response response : this.responses) {
			if(response.getText().equals(text)) {
				return Optional.of(response);
			}
		}
		return Optional.ofNullable(null);
	}
	
}
