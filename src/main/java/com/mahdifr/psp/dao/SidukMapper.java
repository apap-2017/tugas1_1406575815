package com.mahdifr.psp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.mahdifr.psp.model.KecamatanModel;
import com.mahdifr.psp.model.KeluargaModel;
import com.mahdifr.psp.model.KelurahanModel;
import com.mahdifr.psp.model.KotaModel;
import com.mahdifr.psp.model.PendudukModel;

@Mapper
public interface SidukMapper {
	/*
	 * Bottom -> Up : Penduduk-Kota
	 * Fitur 1
	 */
	@Select("SELECT * FROM kota WHERE id=#{id}")
	KotaModel selectKota(@Param("id") String id);
	
	@Select("SELECT * FROM kecamatan WHERE id=#{id}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="kode_kecamatan", column="kode_kecamatan"),
    		@Result(property="nama_kecamatan", column="nama_kecamatan"),
    		@Result(property="kota", column="id_kota",
    		javaType = KotaModel.class,
    		many=@Many(select="selectKota"))
    		})
	KecamatanModel selectKecamatan(@Param("id") String id);
	
	@Select("SELECT * FROM kelurahan WHERE id=#{id}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="kode_kelurahan", column="kode_kelurahan"),
    		@Result(property="nama_kelurahan", column="nama_kelurahan"),
    		@Result(property="kode_pos", column="kode_pos"),
    		@Result(property="kecamatan", column="id_kecamatan",
    		javaType = KecamatanModel.class,
    		many=@Many(select="selectKecamatan"))
    		})
	KelurahanModel selectKelurahan(@Param("id") String id);
	
	@Select("SELECT * FROM keluarga WHERE id=#{id}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="nomor_kk", column="nomor_kk"),
    		@Result(property="alamat", column="alamat"),
    		@Result(property="rt", column="rt"),
    		@Result(property="rw", column="rw"),
    		@Result(property="is_tidak_berlaku", column="is_tidak_berlaku"),
    		@Result(property="kelurahan", column="id_kelurahan",
    		javaType = KelurahanModel.class,
    		many=@Many(select="selectKelurahan"))
    		})
	KeluargaModel selectKeluarga(@Param("id") String id); // Untuk fitur 3 juga
	
	@Select("SELECT * FROM penduduk WHERE nik=#{nik}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="id_keluarga", column="id_keluarga"),
    		@Result(property="nik", column="nik"),
    		@Result(property="nama", column="nama"),
    		@Result(property="tempat_lahir", column="tempat_lahir"),
    		@Result(property="tanggal_lahir", column="tanggal_lahir"),
    		@Result(property="jenis_kelamin", column="jenis_kelamin"),
    		@Result(property="is_wni", column="is_wni"),
    		@Result(property="agama", column="agama"),
    		@Result(property="pekerjaan", column="pekerjaan"),
    		@Result(property="status_perkawinan", column="status_perkawinan"),
    		@Result(property="status_dalam_keluarga", column="status_dalam_keluarga"),
    		@Result(property="golongan_darah", column="golongan_darah"),
    		@Result(property="is_wafat", column="is_wafat"),
    		@Result(property="keluarga", column="id_keluarga",
    		javaType = KeluargaModel.class,
    		many=@Many(select="selectKeluarga"))
    		})
	PendudukModel selectPenduduk(@Param("nik") String nik);
	/*
	 * End of Bottom -> Up : Penduduk-Kota
	 */
	
	/*
	 * Top -> Down : Keluarga-Penduduk
	 * Fitur 2
	 */
	@Select("SELECT * FROM keluarga WHERE nomor_kk=#{nkk}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="nomor_kk", column="nomor_kk"),
    		@Result(property="alamat", column="alamat"),
    		@Result(property="rt", column="rt"),
    		@Result(property="rw", column="rw"),
    		@Result(property="is_tidak_berlaku", column="is_tidak_berlaku"),
    		@Result(property="kelurahan", column="id_kelurahan",
    		javaType = KelurahanModel.class,
    		many=@Many(select="selectKelurahan")),
    		@Result(property="penduduk", column="id",
    		javaType = List.class,
    		many=@Many(select="selectListAnggotaKeluarga"))
    		})
	KeluargaModel selectDetailKeluarga(@Param("nkk") String nkk);
	
	@Select("SELECT * FROM penduduk WHERE id_keluarga=#{nkk}")
	List<PendudukModel> selectListAnggotaKeluarga(@Param("nkk") String nkk);
	/*
	 * End of Top -> Down : Keluarga-Penduduk
	 */
	
	/*
	 * Fitur 3
	 */
	@Select("SELECT nik FROM penduduk WHERE nik BETWEEN #{minNik} AND #{maxNik}"
			+ "ORDER BY nik DESC LIMIT 1")
	String selectLastUrutanPenduduk(@Param("minNik") String minNik, @Param("maxNik") String maxNik);
	
	@Insert("INSERT INTO penduduk (jenis_kelamin, id_keluarga, nik, nama, tempat_lahir, agama, pekerjaan, status_perkawinan, "
			+ "status_dalam_keluarga, golongan_darah, tanggal_lahir, is_wni, is_wafat) VALUES (#{jenis_kelamin}, #{id_keluarga}, #{nik}, #{nama}, "
			+ "#{tempat_lahir}, #{agama}, #{pekerjaan}, #{status_perkawinan}, #{status_dalam_keluarga}, #{golongan_darah}, #{tanggal_lahir}, "
			+ "#{is_wni}, #{is_wafat})")
	void insertPenduduk(PendudukModel penduduk);
	
	/*
	 * Fitur 4
	 */
	@Select("SELECT nomor_kk FROM keluarga WHERE nomor_kk BETWEEN #{minNkk} AND #{maxNkk}"
			+ "ORDER BY nomor_kk DESC LIMIT 1")
	String selectLastUrutanKeluarga(@Param("minNkk") String minNkk, @Param("maxNkk") String maxNkk);
	
	@Insert("INSERT INTO keluarga (nomor_kk, alamat, rt, rw, id_kelurahan) "
			+ "VALUES (#{nomor_kk}, #{alamat}, #{rt}, #{rw}, #{id_kelurahan})")
	void insertKeluarga(KeluargaModel keluarga);
	
	/*
	 * Dependency Dropdown Domisili
	 * Top -> Down : Kota-Kelurahan
	 */
	@Select("SELECT * FROM kota")
	List<KotaModel> selectListKota();
	
	@Select("SELECT * FROM kota WHERE id=#{idKota}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="kode_kota", column="kode_kota"),
    		@Result(property="nama_kota", column="nama_kota"),
    		@Result(property="kecamatan", column="id",
    		javaType = List.class,
    		many=@Many(select="selectListKecamatan"))
    		})
	KotaModel selectDetailKota(@Param("idKota") String idKota);
	
	@Select("SELECT * FROM kecamatan WHERE id_kota=#{idKota}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="id_kota", column="id_kota"),
    		@Result(property="kode_kecamatan", column="kode_kecamatan"),
    		@Result(property="nama_kecamatan", column="nama_kecamatan"),
    		@Result(property="kelurahan", column="id",
    		javaType = List.class,
    		many=@Many(select="selectListKelurahan"))
    		})
	List<KecamatanModel> selectListKecamatan(@Param("idKota") String idKota);
	
	@Select("SELECT * FROM kecamatan WHERE id=#{idKecamatan}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="id_kota", column="id_kota"),
    		@Result(property="kode_kecamatan", column="kode_kecamatan"),
    		@Result(property="nama_kecamatan", column="nama_kecamatan"),
    		@Result(property="kelurahan", column="id",
    		javaType = List.class,
    		many=@Many(select="selectListKelurahan"))
    		})
	KecamatanModel selectDetailKecamatan(@Param("idKecamatan") String idKecamatan);
	
	@Select("SELECT * FROM kelurahan WHERE id_kecamatan=#{idKecamatan}")
	List<KelurahanModel> selectListKelurahan(@Param("idKecamatan") String idKecamatan);
	/*
	 * End of Dependency Dropdown Domisili
	 */
	
	/*
	 * Fitur 5
	 */
	@Update("UPDATE penduduk SET nik=#{nik}, nama=#{nama}, tempat_lahir=#{tempat_lahir}, "
			+ "tanggal_lahir=#{tanggal_lahir}, jenis_kelamin=#{jenis_kelamin}, is_wni=#{is_wni}, "
			+ "id_keluarga=#{id_keluarga}, agama=#{agama}, pekerjaan=#{pekerjaan}, status_perkawinan=#{status_perkawinan}, "
			+ "status_dalam_keluarga=#{status_dalam_keluarga}, golongan_darah=#{golongan_darah}, is_wafat=#{is_wafat} "
			+ "WHERE id=#{id}")
	void updatePenduduk(PendudukModel penduduk);
	
	/*
	 * Fitur 6
	 */
	@Select("SELECT * FROM keluarga WHERE nomor_kk=#{nkk}")
	@Results(value = {
    		@Result(property="id", column="id"),
    		@Result(property="nomor_kk", column="nomor_kk"),
    		@Result(property="alamat", column="alamat"),
    		@Result(property="rt", column="rt"),
    		@Result(property="rw", column="rw"),
    		@Result(property="is_tidak_berlaku", column="is_tidak_berlaku"),
    		@Result(property="id_kelurahan", column="id_kelurahan"),
    		@Result(property="kelurahan", column="id_kelurahan",
    		javaType = KelurahanModel.class,
    		many=@Many(select="selectKelurahan"))
    		})
	KeluargaModel selectDataKeluarga(@Param("nkk") String nkk);
	
	@Select("SELECT * FROM kecamatan WHERE id_kota=#{idKota}")
	List<KecamatanModel> getListKecamatan(@Param("idKota") String idKota);
	
	@Select("SELECT * FROM kelurahan WHERE id_kecamatan=#{idKecamatan}")
	List<KelurahanModel> getListKelurahan(@Param("idKecamatan") String idKecamatan);
	
	@Update("UPDATE keluarga SET nomor_kk=#{nomor_kk}, alamat=#{alamat}, rt=#{rw}, "
			+ "rw=#{rw}, id_kelurahan=#{id_kelurahan} WHERE id=#{id}")
	void updateKeluarga(KeluargaModel keluarga);
	
	@Select("SELECT * FROM penduduk WHERE nik=#{nik}")
	PendudukModel getPenduduk(String nik);
	
	/*
	 * Fitur 7
	 */
	@Update("UPDATE penduduk SET is_wafat=1 WHERE nik=#{nik}")
	void setWafat(@Param("nik") String nik);
	
	@Select("SELECT count(is_wafat) FROM keluarga JOIN penduduk "
			+ "ON (keluarga.id=penduduk.id_keluarga) WHERE keluarga.nomor_kk=#{nkk} AND penduduk.is_wafat=0")
	int countJumlahAnggotaKeluargaHidup(@Param("nkk") String nkk);
	
	@Update("UPDATE keluarga SET is_tidak_berlaku=1 WHERE nomor_kk=#{nkk}")
	void setTidakBerlaku(@Param("nkk") String nkk);
	
	/*
	 * Fitur 8
	 */
	@Select("SELECT p.nik, p.nama, p.jenis_kelamin FROM penduduk p JOIN keluarga k ON p.id_keluarga=k.id "
			+ "JOIN kelurahan kel ON k.id_kelurahan=kel.id WHERE kel.id=#{idKelurahan}")
	List<PendudukModel> getListPendudukDaerah(@Param("idKelurahan") String idKelurahan);
	
	@Select("SELECT p.nik, p.nama, p.tanggal_lahir FROM penduduk p JOIN keluarga k ON p.id_keluarga=k.id "
			+ "JOIN kelurahan kel ON k.id_kelurahan=kel.id WHERE kel.id=#{idKelurahan} ORDER BY p.tanggal_lahir DESC LIMIT 1")
	PendudukModel getPendudukTermudaDaerah(@Param("idKelurahan") String idKelurahan);
	
	@Select("SELECT p.nik, p.nama, p.tanggal_lahir FROM penduduk p JOIN keluarga k ON p.id_keluarga=k.id "
			+ "JOIN kelurahan kel ON k.id_kelurahan=kel.id WHERE kel.id=#{idKelurahan} ORDER BY p.tanggal_lahir ASC LIMIT 1")
	PendudukModel getPendudukTertuaDaerah(@Param("idKelurahan") String idKelurahan);
}
