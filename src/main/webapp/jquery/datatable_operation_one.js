var $dTable = $();

function init_dataTable(){
	
		$dTable = $('#fullList').dataTable({
			"sPaginationType": "two_button",
			"bFilter": true,
			"aoColumnDefs": [
		                        { "bVisible": false, "aTargets": [ 0 ] }
		                    ] ,
		    "oLanguage": {
		        "sSearch": "Filtruj wiersze: ",
		        "sZeroRecords": "Brak danych do wyświetlenia",
		        "sInfoEmpty": "Brak danych do wyświetlenia",
		        "sEmptyTable": "Brak danych do wyświetlenia",
		        "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
		        "oPaginate": {
		        	"sPrevious": "<<",
			        "sNext": ">>",
			        "sFirst": "Początek",
			        "sLast": "Koniec",
		        },
		        "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
		        "sLengthMenu": 'Pokaż <select>'+
		        '<option value="10">40</option>'+
		        '<option value="20">80</option>'+
		        '<option value="30">120</option>'+
		        '<option value="-1">całość</option>'+
		        '</select> wierszy'
		        
		      }
		    });
	
	
	
	$dTable.setClickRow = function(){
		$dTable.children('tbody').children('tr').click(function() {
			var data =  $dTable.fnGetData(this);
			$('#formAdd').dialog('open');
			$dTable.insertData(data);
		});
	}
	
	$dTable.refreshClickRow = function(){
		$dTable.children('tbody').children('tr').unbind('click').click(function() {
			var data =  $dTable.fnGetData(this);
			$('#formAdd').dialog('open');
			$dTable.insertData(data);
		});
	}
	
	$dTable.tableSize = $dTable.fnGetNodes().length;
	
	
	$dTable.getPosition = function(id) {
		var tr = $dTable.getTrNodeContainsId(id);
		if (tr) {
			return $dTable.fnGetPosition(tr);
		} else
			return -1;
	}
	
	$dTable.getTrNodeContainsId = function (id) {
		var trNodes = $dTable.fnGetNodes();

		for (i in trNodes) {
			var idNode = $(trNodes[i]).children('td').first();
			if (idNode.text() == id)
				return trNodes[i];
		}
	}	
	
	 $dTable.deleteRow = function(id) {
         var position = $dTable.getPosition(id);
         $dTable.fnDeleteRow(position);
         $('#formAdd').dialog('close');
     }
	 
	 $dTable.insertRow =  function(id) {   
             var position = $dTable.getPosition(id);
             var data = $dTable.getDataArray(id);
             if (position >= 0 && position < $dTable.tableSize) {
            	 $dTable.showAll();
                 $dTable.fnUpdate(data, position, 0, false);
                
             } else {
                 var tr = $dTable.fnAddData(data);
                 $dTable.tableSize++;
                 $dTable.refreshClickRow();
                 
             }
             $('#formAdd').dialog('close');
      }
	 
	$dTable.editForm = document.getElementById('formAdd');
	$dTable.setClickRow();
	$dTable.idInput = document.getElementById('id');
}

function setSelectedIndex(select,toCompare){
	for(i in select.options) {
        if(select.options[i].value == toCompare){
            select.options[i].setAttribute("selected","selected");
        }
	}
}

function setSelectedIndexWithInner(select,toCompare){
	for(i in select.options) {
        if(select.options[i].innerHTML == toCompare){
            select.options[i].setAttribute("selected","selected");
        }
	}
}


function resetForm() {document.getElementsByTagName('form')[0].reset();}

function showAddRow() { 
    resetForm();
    $('#formAdd').dialog('open');
}       


	
	
