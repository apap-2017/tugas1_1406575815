package com.mahdifr.psp.controller;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mahdifr.psp.model.KecamatanModel;
import com.mahdifr.psp.model.KeluargaModel;
import com.mahdifr.psp.model.KelurahanModel;
import com.mahdifr.psp.model.KotaModel;
import com.mahdifr.psp.model.PendudukModel;
import com.mahdifr.psp.service.SidukService;

//@Slf4j
@Controller
public class SidukController {
	// Initiate References Attributes
	private final List<String> GOLONGAN_DARAH = Arrays.asList("A-", "A+", "B-", "B+", "AB-", "AB+", "O-", "O+");
	private final List<String> AGAMA = Arrays.asList("Islam", "Kristen", "Katholik", "Hindu", "Budha", "Konghucu");
	private final List<String> STATUS_PERKAWINAN = Arrays.asList("Kawin", "Belum Kawin", "Cerai Mati", "Cerai Hidup");
	private final List<String> STATUS_DALAM_KELUARGA = Arrays.asList("Kepala Keluarga", "Istri", "Anak", "Famili Lain", "Pembantu");
	
	@Autowired
	private SidukService sidukDAO;
	
	@RequestMapping(value="/")
	public String home() {
		return "home";
	}
	
	/*
	 * Fitur 1
	 * @param String NIK
	 */
	@RequestMapping(value="/penduduk", method=RequestMethod.GET)
	public String selectPenduduk(@RequestParam(value="nik", required=true) String nik, Model model) {
		if(nik.matches("\\d*")) {
			PendudukModel archive = sidukDAO.getBottomUpPenduduk(nik);
			if(archive == null) {
				model.addAttribute("errorNik", "NIK tidak ditemukan");
				return "home";
			} else {
				archive.setTanggal_lahir(reFormatStringYMDtoDMY(archive.getTanggal_lahir()));
				model.addAttribute("archive", archive);
				return "view-penduduk";			
			}			
		} else {
			model.addAttribute("errorNik", "Harus berisi angka");
			return "home";
		}
	}
	
	/*
	 * Fitur 2
	 * @param String NKK
	 */
	@RequestMapping(value="/keluarga", method=RequestMethod.GET)
	public String selectKeluarga(@RequestParam(value="nkk", required=true) String nkk, Model model) {
		if(nkk.matches("\\d*")) {
			KeluargaModel archive = sidukDAO.getTopDownKeluarga(nkk);
			if(archive == null) {
				model.addAttribute("errorNkk", "NKK tidak ditemukan");
				return "home";
			} else {
				model.addAttribute("archive", archive);
				return "view-detail-keluarga";			
			}
		} else {
			model.addAttribute("errorNkk", "Harus berisi angka");
			return "home";
		}
	}
	
	/*
	 * Fitur 3
	 * Insert Penduduk
	 */
	@RequestMapping(value="/penduduk/tambah", method=RequestMethod.GET)
	public String formPenduduk(PendudukModel penduduk, Model model) {
		return "form-add-penduduk";
	}
	
	/*
	 * @param PendudukModel
	 */
	@RequestMapping(value="/penduduk/tambah", method=RequestMethod.POST)
	public String addPenduduk(@Valid PendudukModel penduduk, BindingResult bindingResult, Model model) {
		KeluargaModel archive = sidukDAO.getDataKeluarga(String.valueOf(penduduk.getId_keluarga()));
		if(archive == null) {
			model.addAttribute("errorIdKeluarga", "Id Keluarga Tidak Terdaftar");
			return "form-add-penduduk";
		} else {
			if(bindingResult.hasErrors()) {
				return "form-add-penduduk";
			} else {
				// Construct NIK
				String nik = constructNik(archive.getKelurahan().getKecamatan().getKode_kecamatan(),
						constructTanggal(penduduk.getTanggal_lahir()), penduduk.getJenis_kelamin());
				penduduk.setNik(nik);
				penduduk.setTanggal_lahir(reFormatStringDMYtoYMD(penduduk.getTanggal_lahir()));
				sidukDAO.insertPenduduk(penduduk);
				model.addAttribute("title", "Success Add Penduduk");
				model.addAttribute("message", "Penduduk dengan NIK " + nik + " berhasil ditambahkan");
				return "success-generic";				
			}
		}
	}
	
	/*
	 * Fitur 4
	 * Insert Keluarga
	 */
	@RequestMapping(value="/keluarga/tambah", method=RequestMethod.GET)
	public String formKeluarga(KeluargaModel keluarga, Model model) {
		// Mengirimkan list kota sebagai initiate dependency dropdown
		List<KotaModel> listKota = sidukDAO.getListKota();
		model.addAttribute("kotaModel", listKota);
		return "form-add-keluarga";
	}
	
	/*
	 * @param KeluargaModel
	 */
	@RequestMapping(value="/keluarga/tambah", method=RequestMethod.POST)
	public String addKeluarga(@Valid KeluargaModel keluarga, BindingResult bindingResult, Model model) {
		if(keluarga.getId_kelurahan() == null) {
			List<KotaModel> listKota = sidukDAO.getListKota();
			model.addAttribute("kotaModel", listKota);
			model.addAttribute("errorRequired", "Harus Diisi");
			return "form-add-keluarga";
		} else {
			if(bindingResult.hasErrors()) {
				List<KotaModel> listKota = sidukDAO.getListKota();
				model.addAttribute("kotaModel", listKota);
				return "form-add-keluarga";
			} else {
				// Select kelurahan untuk kode_kecamatan
				KelurahanModel archive = sidukDAO.getBottomUpKelurahan(String.valueOf(keluarga.getId_kelurahan()));
				Date currentDate = new Date();
				String stringDate = constructTanggal(currentDate);
				// Construct NKK
				String nkk = constructNkk(archive.getKecamatan().getKode_kecamatan(), stringDate);
				keluarga.setNomor_kk(nkk);
				sidukDAO.insertKeluarga(keluarga);
				model.addAttribute("title", "Success Add Keluarga");
				model.addAttribute("message", "Keluarga dengan NKK " + nkk + " berhasil ditambahkan");
				return "success-generic";
			}
		}
	}
	
	/*
	 * Fitur 5
	 * Update Penduduk
	 * @param String NIK
	 */
	@RequestMapping(value="/penduduk/ubah/{nik}", method=RequestMethod.GET)
	public String formUbahPenduduk(@PathVariable(value = "nik") String nik, Model model) {
		// Select penduduk
		PendudukModel penduduk = sidukDAO.getBottomUpPenduduk(nik);
		if(penduduk == null) {
			return "not-found";
		} else {
			penduduk.setTanggal_lahir(reFormatStringYMDtoDMY(penduduk.getTanggal_lahir()));
			// Send References Attributes
			model.addAttribute("pendudukModel", penduduk);
			model.addAttribute("golonganDarah", GOLONGAN_DARAH);
			model.addAttribute("agama", AGAMA);
			model.addAttribute("statusPerkawinan", STATUS_PERKAWINAN);
			model.addAttribute("statusDalamKeluarga", STATUS_DALAM_KELUARGA);
			return "form-update-penduduk";
		}
	}
	
	/*
	 * @param String NIK
	 * @param PendudukModel
	 */
	@RequestMapping(value="/penduduk/ubah/{nik}", method=RequestMethod.POST)
	public String ubahPenduduk(@PathVariable(value = "nik") String nik, @Valid PendudukModel penduduk, BindingResult bindingResult, Model model) {
		KeluargaModel archive = sidukDAO.getDataKeluarga(String.valueOf(penduduk.getId_keluarga()));
		if(archive == null) {
			model.addAttribute("errorIdKeluarga", "Id Keluarga Tidak Terdaftar");
			model.addAttribute("pendudukModel", penduduk);
			model.addAttribute("golonganDarah", GOLONGAN_DARAH);
			model.addAttribute("agama", AGAMA);
			model.addAttribute("statusPerkawinan", STATUS_PERKAWINAN);
			model.addAttribute("statusDalamKeluarga", STATUS_DALAM_KELUARGA);
			return "form-update-penduduk";
		} else {
			if(bindingResult.hasErrors()) {
				model.addAttribute("pendudukModel", penduduk);
				model.addAttribute("golonganDarah", GOLONGAN_DARAH);
				model.addAttribute("agama", AGAMA);
				model.addAttribute("statusPerkawinan", STATUS_PERKAWINAN);
				model.addAttribute("statusDalamKeluarga", STATUS_DALAM_KELUARGA);
				return "form-update-penduduk";
			} else {
				PendudukModel oldPenduduk = sidukDAO.getBottomUpPenduduk(nik);
				String newNik = "";
				// Update NIK jika tanggal lahir atau id_keluarga berubah
				if(!penduduk.getTanggal_lahir().equals(reFormatStringYMDtoDMY(oldPenduduk.getTanggal_lahir())) || 
						penduduk.getId_keluarga() != oldPenduduk.getId_keluarga()) {
					// Construct new NIK
					newNik = constructNik(archive.getKelurahan().getKecamatan().getKode_kecamatan(),
							constructTanggal(penduduk.getTanggal_lahir()), penduduk.getJenis_kelamin());
				} else {
					newNik = nik;
				}
				// Update database
				penduduk.setId(oldPenduduk.getId());
				penduduk.setNik(newNik);
				penduduk.setTanggal_lahir(reFormatStringDMYtoYMD(penduduk.getTanggal_lahir()));
				sidukDAO.updatePenduduk(penduduk);
				model.addAttribute("title", "Success Update Penduduk");
				model.addAttribute("message", "Penduduk dengan NIK " + nik + " berhasil diubah");
				return "success-generic";		
			}
		}
	}
	
	/*
	 * Fitur 6
	 * Update Keluarga
	 * @param String NKK
	 */
	@RequestMapping(value="/keluarga/ubah/{nkk}", method=RequestMethod.GET)
	public String formUbahKeluarga(@PathVariable(value="nkk") String nkk, Model model) {
		// Select keluarga
		KeluargaModel keluarga = sidukDAO.getBottomUpKeluarga(nkk);
		if(keluarga == null) {
			return "not-found";
		} else {
			// Select kota, kecamatan, dan kelurahan untuk auto fill dependency dropdown
			List<KotaModel> listKota = sidukDAO.getListKota();
			List<KecamatanModel> listKecamatan = sidukDAO.getListKecamatan(String.valueOf(keluarga.getKelurahan().getKecamatan().getKota().getId()));
			List<KelurahanModel> listKelurahan = sidukDAO.getListKelurahan(String.valueOf(keluarga.getKelurahan().getKecamatan().getId()));
			model.addAttribute("keluargaModel", keluarga);
			model.addAttribute("kotaModel", listKota);
			model.addAttribute("kecamatanModel", listKecamatan);
			model.addAttribute("kelurahanModel", listKelurahan);
			return "form-update-keluarga";
		}
	}
	
	/*
	 * @param String NKK
	 * @param KeluargaModel
	 */
	@RequestMapping(value="/keluarga/ubah/{nkk}", method=RequestMethod.POST)
	public String ubahKeluarga(@PathVariable(value="nkk") String nkk, @Valid KeluargaModel keluarga, BindingResult bindingResult, Model model) {
		if(keluarga.getId_kelurahan() == null) {
			KeluargaModel keluargaArchive = sidukDAO.getBottomUpKeluarga(nkk);
			List<KotaModel> listKota = sidukDAO.getListKota();
			List<KecamatanModel> listKecamatan = sidukDAO.getListKecamatan(String.valueOf(keluargaArchive.getKelurahan().getKecamatan().getKota().getId()));
			List<KelurahanModel> listKelurahan = sidukDAO.getListKelurahan(String.valueOf(keluargaArchive.getKelurahan().getKecamatan().getId()));
			model.addAttribute("keluargaModel", keluargaArchive);
			model.addAttribute("kotaModel", listKota);
			model.addAttribute("kecamatanModel", listKecamatan);
			model.addAttribute("kelurahanModel", listKelurahan);
			model.addAttribute("errorRequired", "Harus Diisi");
			return "form-update-keluarga";
		} else {
			if(bindingResult.hasErrors()) {
				KeluargaModel keluargaArchive = sidukDAO.getBottomUpKeluarga(nkk);
				List<KotaModel> listKota = sidukDAO.getListKota();
				List<KecamatanModel> listKecamatan = sidukDAO.getListKecamatan(String.valueOf(keluargaArchive.getKelurahan().getKecamatan().getKota().getId()));
				List<KelurahanModel> listKelurahan = sidukDAO.getListKelurahan(String.valueOf(keluargaArchive.getKelurahan().getKecamatan().getId()));
				model.addAttribute("keluargaModel", keluargaArchive);
				model.addAttribute("kotaModel", listKota);
				model.addAttribute("kecamatanModel", listKecamatan);
				model.addAttribute("kelurahanModel", listKelurahan);
				return "form-update-keluarga";			
			} else {
				// Construct new NKK
				KelurahanModel archive = sidukDAO.getBottomUpKelurahan(String.valueOf(keluarga.getId_kelurahan()));
				Date currentDate = new Date();
				String stringDate = constructTanggal(currentDate);
				KeluargaModel oldKeluarga = sidukDAO.getBottomUpKeluarga(nkk);
				String newNkk = constructNkk(archive.getKecamatan().getKode_kecamatan(), stringDate);
				keluarga.setId(oldKeluarga.getId());
				keluarga.setNomor_kk(newNkk);
				// Update NIK penduduk jika id_kelurahan berubah
				if(keluarga.getId_kelurahan() != oldKeluarga.getId_kelurahan()) {
					KeluargaModel listAnggotaKeluarga = sidukDAO.getTopDownKeluarga(nkk);
					for(PendudukModel penduduk : listAnggotaKeluarga.getPenduduk()) {
						String newNik = constructNik(archive.getKecamatan().getKode_kecamatan(),
								constructTanggal(reFormatStringYMDtoDMY(penduduk.getTanggal_lahir())), penduduk.getJenis_kelamin());
						penduduk.setNik(newNik);
						sidukDAO.updatePenduduk(penduduk);
					}
				}
				sidukDAO.updateKeluarga(keluarga);
				model.addAttribute("title", "Success Update Keluarga");
				model.addAttribute("message", "Keluarga dengan NKK " + nkk + " berhasil diubah");
				return "success-generic";
			}
		}
		
	}
	
	/*
	 * Fitur 7
	 * Set Kematian
	 * @param String NIK
	 */
	@RequestMapping(value="/penduduk/mati", method=RequestMethod.POST)
	public String ubahStatusKematian(@RequestParam(value="nik") String nik) {
		sidukDAO.setWafat(nik);
		// Set Tidak berlaku jika sudah tidak ada anggota keluarga yang masih hidup
		PendudukModel penduduk = sidukDAO.getBottomUpPenduduk(nik);
		int countHidup = sidukDAO.countJumlahAnggotaKeluargaHidup(penduduk.getKeluarga().getNomor_kk());
		if(countHidup == 0) {
			sidukDAO.setTidakBerlaku(penduduk.getKeluarga().getNomor_kk());
		}
		return "redirect:/penduduk?nik=" + nik;
	}
	
	/*
	 * Fitur 8
	 * Search Penduduk di suatu daerah
	 */
	@RequestMapping(value="/penduduk/cari", method=RequestMethod.GET)
	public String cariPenduduk(Model model,
			@RequestParam(value="kt") Optional<String> kt,
			@RequestParam(value="kc") Optional<String> kc,
			@RequestParam(value="kl") Optional<String> kl) {
		if(kt.isPresent()) {
			if(kc.isPresent()) {
				if(kl.isPresent()) {
					// Return data table
					List<PendudukModel> archive = sidukDAO.getListPendudukDaerah(kl.get());
					model.addAttribute("pendudukModel", archive);
					// Penduduk paling muda
					PendudukModel termuda = sidukDAO.getPendudukTermudaDaerah(kl.get());
					model.addAttribute("pendudukTermuda", termuda);
					// Penduduk paling tua
					PendudukModel tertua = sidukDAO.getPendudukTertuaDaerah(kl.get());
					model.addAttribute("pendudukTertua", tertua);
					return "view-penduduk-by-daerah";
				}
				// Tidak mendapat kiriman kelurahan
				// Autofill untuk yang telah diisi
				List<KotaModel> listKota = sidukDAO.getListKota();
				List<KecamatanModel> listKecamatan = sidukDAO.getListKecamatan(kt.get());
				List<KelurahanModel> listKelurahan = sidukDAO.getListKelurahan(kc.get());
				model.addAttribute("kotaModel", listKota);
				model.addAttribute("selectedKota", kt.get());
				model.addAttribute("kecamatanModel", listKecamatan);
				model.addAttribute("selectedKecamatan", kc.get());
				model.addAttribute("kelurahanModel", listKelurahan);
				model.addAttribute("errorKelurahan", "Harus Diisi");
				return "form-cari-penduduk";
			}
			// Tidak mendapat kiriman kecamatan
			// Autofill untuk yang telah diisi
			List<KotaModel> listKota = sidukDAO.getListKota();
			List<KecamatanModel> listKecamatan = sidukDAO.getListKecamatan(kt.get());
			model.addAttribute("kotaModel", listKota);
			model.addAttribute("selectedKota", kt.get());
			model.addAttribute("kecamatanModel", listKecamatan);
			model.addAttribute("errorKecamatan", "Harus Diisi");
			model.addAttribute("errorKelurahan", "Harus Diisi");
			return "form-cari-penduduk";
		} else {
			// Initial Page atau kirim tapi tidak diisi
			// Mengirimkan list kota sebagai initiate dependency dropdown
			List<KotaModel> listKota = sidukDAO.getListKota();
			model.addAttribute("kotaModel", listKota);
			return "form-cari-penduduk";
		}
	}
	
	/*
	 * Helper Method
	 * Construct NIK
	 */
	private String constructNik(String kodeDaerah, String tanggal, int jenis_kelamin) {
		String newTanggal = "";
		if(jenis_kelamin == 1) {
			newTanggal = String.valueOf(Integer.parseInt(tanggal.substring(0,2))+40) + tanggal.substring(2, tanggal.length());			
		} else {
			newTanggal = tanggal;
		}
		String minNik = kodeDaerah.substring(0, 6) + newTanggal.substring(0, 4) + newTanggal.substring(newTanggal.length()-2, newTanggal.length()) + "0001";
		String maxNik = String.valueOf(Long.parseLong(minNik)+9999);
		// Search nomor urut terakhir NIK
		String lastNoUrutNik = sidukDAO.getLastUrutanPenduduk(minNik, maxNik);
		if(lastNoUrutNik == null) {
			return minNik;
		} else {
			return String.valueOf(Long.parseLong(lastNoUrutNik)+1);
		}
	}
	
	/*
	 * Helper Method
	 * Construct NKK
	 */
	private String constructNkk(String kodeDaerah, String tanggal) {
		String minNkk = kodeDaerah.substring(0, 6) + tanggal.substring(0, 4) + tanggal.substring(tanggal.length()-2, tanggal.length()) + "0001";
		String maxNkk = String.valueOf(Long.parseLong(minNkk)+9999);
		// Search nomor urut terakhir NKK
		String lastNoUrutNkk = sidukDAO.getLastUrutanKeluarga(minNkk, maxNkk);
		if(lastNoUrutNkk == null) {
			return minNkk;
		} else {
			return String.valueOf(Long.parseLong(lastNoUrutNkk)+1);
		}
	}
	
	/*
	 * Helper Method
	 * Manipulate Tanggal
	 */
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
	
	/*
	 * Helper Method
	 * Provide JSON untuk dependency dropdown
	 */
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
	    return sidukDAO.getTopDownKecamatan(id_kecamatan);
	}
}
