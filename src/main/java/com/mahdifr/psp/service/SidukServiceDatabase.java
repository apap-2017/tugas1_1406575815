package com.mahdifr.psp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahdifr.psp.dao.SidukMapper;
import com.mahdifr.psp.model.KecamatanModel;
import com.mahdifr.psp.model.KeluargaModel;
import com.mahdifr.psp.model.KelurahanModel;
import com.mahdifr.psp.model.KotaModel;
import com.mahdifr.psp.model.PendudukModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SidukServiceDatabase implements SidukService {
	@Autowired
	private SidukMapper sidukMapper;

	@Override
	public PendudukModel getBottomUpPenduduk(String nik) {
		log.info ("select penduduk with nik {}", nik);
        return sidukMapper.selectPenduduk(nik);
	}

	@Override
	public KeluargaModel getTopDownKeluarga(String nkk) {
		log.info ("select keluarga with nkk {}", nkk);
        return sidukMapper.selectDetailKeluarga(nkk);
	}

	@Override
	public KeluargaModel getDataKeluarga(String id) {
		log.info ("select keluarga with id {}", id);
        return sidukMapper.selectKeluarga(id);
	}

	@Override
	public String getLastUrutanPenduduk(String minNik, String maxNik) {
		log.info ("get max nik between {} and {}", minNik, maxNik);
        return sidukMapper.selectLastUrutanPenduduk(minNik, maxNik);
	}

	@Override
	public void insertPenduduk(PendudukModel penduduk) {
		log.info ("insert new penduduk {}", penduduk);
        sidukMapper.insertPenduduk(penduduk);
	}

	@Override
	public List<KotaModel> getListKota() {
		log.info ("get list kota");
        return sidukMapper.selectListKota();
	}

	@Override
	public KotaModel getTopDownKota(String id) {
		log.info ("get detail kota {}", id);
        return sidukMapper.selectDetailKota(id);
	}

	@Override
	public KecamatanModel getTopDownKecamatan(String id) {
		log.info ("get detail kecamatan {}", id);
        return sidukMapper.selectDetailKecamatan(id);
	}

	@Override
	public KelurahanModel getBottomUpKelurahan(String id) {
		log.info ("get data kelurahan {}", id);
        return sidukMapper.selectKelurahan(id);
	}

	@Override
	public String getLastUrutanKeluarga(String minNkk, String maxNkk) {
		log.info ("get max nkk between {} and {}", minNkk, maxNkk);
        return sidukMapper.selectLastUrutanKeluarga(minNkk, maxNkk);
	}

	@Override
	public void insertKeluarga(KeluargaModel keluarga) {
		log.info ("insert new keluarga {}", keluarga);
        sidukMapper.insertKeluarga(keluarga);
	}

	@Override
	public void updatePenduduk(PendudukModel penduduk) {
		log.info ("update penduduk {}", penduduk);
        sidukMapper.updatePenduduk(penduduk);
	}
}
