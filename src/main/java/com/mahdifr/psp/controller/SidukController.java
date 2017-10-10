package com.mahdifr.psp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.mahdifr.psp.model.KecamatanModel;
import com.mahdifr.psp.model.KeluargaModel;
import com.mahdifr.psp.model.KelurahanModel;
import com.mahdifr.psp.model.KotaModel;
import com.mahdifr.psp.model.PendudukModel;
import com.mahdifr.psp.service.SidukService;

//@Slf4j
@Controller
public class SidukController {
	@Autowired
	private SidukService sidukDAO;
	
	@RequestMapping(value="/")
	public String home() {
		return "home";
	}
	
	@RequestMapping(value="/penduduk", method=RequestMethod.GET)
	public String selectPenduduk(@RequestParam(value="nik", required=true) String nik, Model model) {
		PendudukModel archive = sidukDAO.getBottomUpPenduduk(nik);
		if(archive == null) {
			return "not-found";
		} else {
			archive.setTanggal_lahir(reFormatStringYMDtoDMY(archive.getTanggal_lahir()));
			model.addAttribute("archive", archive);
			return "view-penduduk";			
		}
	}
	
	@RequestMapping(value="/keluarga", method=RequestMethod.GET)
	public String selectKeluarga(@RequestParam(value="nkk", required=true) String nkk, Model model) {
		KeluargaModel archive = sidukDAO.getTopDownKeluarga(nkk);
		if(archive == null) {
			return "not-found";
		} else {
			model.addAttribute("archive", archive);
			return "view-detail-keluarga";			
		}
	}
	
	@RequestMapping(value="/penduduk/tambah", method=RequestMethod.GET)
	public String formPenduduk(PendudukModel penduduk, Model model) {
		return "form-add-penduduk";
	}
	
	@RequestMapping(value="/penduduk/tambah", method=RequestMethod.POST)
	public String addPenduduk(@Valid PendudukModel penduduk, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			return "form-add-penduduk";
		} else {
			/*
			 * select data keluarga
			 * construct nik
			 * select nik tersebut di db, tambahin 10.000 buat cek nomor urut max. ambil max nik nya
			 * construct nik dgn nomor urut terakhir
			 */
			KeluargaModel archive = sidukDAO.getDataKeluarga(String.valueOf(penduduk.getId_keluarga()));
//			Date tanggalLahir = stringToDateDMY(penduduk.getTanggal_lahir());
			String nik = constructNik(archive.getKelurahan().getKecamatan().getKode_kecamatan(),
					constructTanggal(penduduk.getTanggal_lahir()), penduduk.getJenis_kelamin());
			penduduk.setNik(nik);
			penduduk.setTanggal_lahir(reFormatStringDMYtoYMD(penduduk.getTanggal_lahir()));
			sidukDAO.insertPenduduk(penduduk);
			model.addAttribute("nik", nik);
			return "success-add-penduduk";
		}
	}
	
	@RequestMapping(value="/keluarga/tambah", method=RequestMethod.GET)
	public String formKeluarga(KeluargaModel keluarga, Model model) {
		List<KotaModel> listKota = sidukDAO.getListKota();
		model.addAttribute("kotaModel", listKota);
		return "form-add-keluarga";
	}
	
	@RequestMapping(value="/keluarga/tambah", method=RequestMethod.POST)
	public String addKeluarga(@Valid KeluargaModel keluarga, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			return "form-add-keluarga";
		} else {
			KelurahanModel archive = sidukDAO.getBottomUpKelurahan(String.valueOf(keluarga.getId_kelurahan()));
			Date currentDate = new Date();
			String stringDate = constructTanggal(currentDate);
			String nkk = constructNkk(archive.getKecamatan().getKode_kecamatan(), stringDate);
			//Insert to DB
			keluarga.setNomor_kk(nkk);
			sidukDAO.insertKeluarga(keluarga);
			model.addAttribute("nkk", nkk);
			return "success-add-keluarga";
		}
	}
	
	/*
	 * Helper Method
	 */
	private String constructNik(String kodeDaerah, String tanggal, int jenis_kelamin) {
		String newTanggal = "";
		if(jenis_kelamin == 1) {
			newTanggal = String.valueOf(Integer.parseInt(tanggal.substring(0,2))+40) + tanggal.substring(2, tanggal.length());			
		} else {
			newTanggal = tanggal;
		}
		String minNik = kodeDaerah.substring(0, 6) + newTanggal.substring(0, 4) + newTanggal.substring(newTanggal.length()-2, newTanggal.length()) + "0001";
		String maxNik = String.valueOf(Long.parseLong(minNik)+1000);
		String lastNoUrutNik = sidukDAO.getLastUrutanPenduduk(minNik, maxNik);
		if(lastNoUrutNik == null) {
			return minNik;
		} else {
			return String.valueOf(Long.parseLong(lastNoUrutNik)+1);
		}
	}
	
	private String constructNkk(String kodeDaerah, String tanggal) {
		String minNkk = kodeDaerah.substring(0, 6) + tanggal.substring(0, 4) + tanggal.substring(tanggal.length()-2, tanggal.length()) + "0001";
		String maxNkk = String.valueOf(Long.parseLong(minNkk)+1000);
		String lastNoUrutNkk = sidukDAO.getLastUrutanKeluarga(minNkk, maxNkk);
		if(lastNoUrutNkk == null) {
			return minNkk;
		} else {
			return String.valueOf(Long.parseLong(lastNoUrutNkk)+1);
		}
	}
	
	private String constructTanggal(String tanggal) {
		return tanggal.replace("-", "");
	}
	
	private String constructTanggal(Date tanggal) {
		return new SimpleDateFormat("dd-MM-yyyy").format(tanggal).replace("-", "");
	}
	
	private Date stringToDateYMD(String data) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(data);				
		} catch(Exception e) {
			
		}
		return date;
	}
	
	private String dateToStringYMD(Date data) {
		return new SimpleDateFormat("yyyy-MM-dd").format(data);
	}
	
	private Date stringToDateDMY(String data) {
		Date date = null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(data);				
		} catch(Exception e) {
			
		}
		return date;
	}
	
	private String dateToStringDMY(Date data) {
		return new SimpleDateFormat("dd-MM-yyyy").format(data);
	}
	
	private String reFormatStringYMDtoDMY(String data) {
		Date oldDate = stringToDateYMD(data);
		return dateToStringDMY(oldDate);
	}
	
	private String reFormatStringDMYtoYMD(String data) {
		Date oldDate = stringToDateDMY(data);
		return dateToStringYMD(oldDate);
	}
	
	@RequestMapping(value = "/beans/kecamatan", method = RequestMethod.GET)
	public @ResponseBody KotaModel findListKecamatan(
			@RequestParam(value = "id_kota", required = true) String id_kota
			) {
	    return sidukDAO.getTopDownKota(id_kota);
	}
	
	@RequestMapping(value = "/beans/kelurahan", method = RequestMethod.GET)
	public @ResponseBody KecamatanModel findListkelurahan(
			@RequestParam(value = "id_kecamatan", required = true) String id_kecamatan
			) {
		KecamatanModel archive = sidukDAO.getTopDownKecamatan(id_kecamatan);
	    return archive;
	}
}
