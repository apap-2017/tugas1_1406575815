package com.mahdifr.psp.service;

import java.util.List;

import com.mahdifr.psp.model.KecamatanModel;
import com.mahdifr.psp.model.KeluargaModel;
import com.mahdifr.psp.model.KelurahanModel;
import com.mahdifr.psp.model.KotaModel;
import com.mahdifr.psp.model.PendudukModel;

public interface SidukService {
	/*
	 * Fitur 1
	 */
	PendudukModel getBottomUpPenduduk(String nik);
	
	/*
	 * Fitur 2
	 */
	KeluargaModel getTopDownKeluarga(String nkk);
	
	/*
	 * Fitur 3
	 */
	KeluargaModel getDataKeluarga(String id);
	String getLastUrutanPenduduk(String minNik, String maxNik);
	void insertPenduduk(PendudukModel penduduk);
	
	/*
	 * Fitur 4
	 */
	List<KotaModel> getListKota();
	KotaModel getTopDownKota(String id);
	KecamatanModel getTopDownKecamatan(String id);
	KelurahanModel getBottomUpKelurahan(String id);
	String getLastUrutanKeluarga(String minNkk, String maxNkk);
	void insertKeluarga(KeluargaModel keluarga);
	
	/*
	 * Fitur 5
	 */
	void updatePenduduk(PendudukModel penduduk);
	
	/*
	 * Fitur 6
	 */
	KeluargaModel getBottomUpKeluarga(String nkk);
	List<KecamatanModel> getListKecamatan(String idKota);
	List<KelurahanModel> getListKelurahan(String idKecamatan);
	void updateKeluarga(KeluargaModel keluarga);
	KecamatanModel getBottomUpKecamatan(String id);
	PendudukModel getPenduduk(String nik);
	
	/*
	 * Fitur 7
	 */
	void setWafat(String nik);
	int countJumlahAnggotaKeluargaHidup(String nkk);
	void setTidakBerlaku(String nkk);
	
	/*
	 * Fitur 8
	 */
	List<PendudukModel> getListPendudukDaerah(String idKelurahan);
	PendudukModel getPendudukTermudaDaerah(String idKelurahan);
	PendudukModel getPendudukTertuaDaerah(String idKelurahan);
}