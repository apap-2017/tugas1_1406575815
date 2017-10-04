package com.mahdifr.psp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.mahdifr.psp.service.SidukService;

//@Slf4j
@Controller
public class SidukController {
	@Autowired
	private SidukService sidukDAO;
	
	@RequestMapping(value="/penduduk", method=RequestMethod.GET)
	public String selectPenduduk(@RequestParam(value="nik", required=true) String nik, Model model) {
		
		return "viewPenduduk";
	}
}
