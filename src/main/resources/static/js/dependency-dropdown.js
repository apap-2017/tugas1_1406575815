$(document).ready(
	function(){
		$("#kota").change(
			function() {
		        $.getJSON("http://localhost:8080/beans/kecamatan", {
		            id_kota : $(this).val(),
		            ajax : 'true'
		        }, function(data) {
		        	var htmlKecamatan = "";
		            var lenKecamatan = data.kecamatan.length;
		            for (var i = 0; i < lenKecamatan; i++) {
		                htmlKecamatan += '<option value="' + data.kecamatan[i].id + '">'
		                        + data.kecamatan[i].nama_kecamatan + '</option>';
		            }
		            $("#kecamatan").html(htmlKecamatan);
		        });
		    }
		);
		$("#kecamatan").change(
			function() {
		        $.getJSON("http://localhost:8080/beans/kelurahan", {
		            id_kecamatan : $(this).val(),
		            ajax : 'true'
		        }, function(data) {
		        	var htmlKelurahan = "";
		            var lenKelurahan = data.kelurahan.length;
		            for (var i = 0; i < lenKelurahan; i++) {
		                htmlKelurahan += '<option value="' + data.kelurahan[i].id + '">'
		                        + data.kelurahan[i].nama_kelurahan + '</option>';
		            }
		            $("#kelurahan").html(htmlKelurahan);
		        });
		    }
		);
	}
);