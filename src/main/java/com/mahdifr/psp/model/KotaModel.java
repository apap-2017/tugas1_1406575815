package com.mahdifr.psp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KotaModel {
	private Integer id;
	private String kode_kota, nama_kota;
	private List<KecamatanModel> kecamatan;
}