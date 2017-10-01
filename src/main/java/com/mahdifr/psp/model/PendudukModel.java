package com.mahdifr.psp.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendudukModel {
	private int id, jenis_kelamin, id_keluarga;
	private String nik, nama, tempat_lahir, agama, pekerjaan, status_perkawinan, status_dalam_keluarga, golongan_darah;
	private Date  tanggal_lahir;
	private boolean is_wni, is_wafat; 
}
