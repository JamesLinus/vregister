var $dTable = $();

$.fn.dataTableExt.oApi.fnGetAdjacentTr  = function ( oSettings, nTr, bNext )
{
    /* Find the node's position in the aoData store */
    var iCurrent = oSettings.oApi._fnNodeToDataIndex( oSettings, nTr );
      
    /* Convert that to a position in the display array */
    var iDisplayIndex = $.inArray( iCurrent, oSettings.aiDisplay );
    if ( iDisplayIndex == -1 )
    {
        /* Not in the current display */
        return null;
    }
      
    /* Move along the display array as needed */
    iDisplayIndex += (typeof bNext=='undefined' || bNext) ? 1 : -1;
      
    /* Check that it within bounds */
    if ( iDisplayIndex < 0 || iDisplayIndex >= oSettings.aiDisplay.length )
    {
        /* There is no next/previous element */
        return null;
    }
      
    /* Return the target node from the aoData store */
    return oSettings.aoData[ oSettings.aiDisplay[ iDisplayIndex ] ].nTr;
};


function init_dataTable(){
	
	$dTable = $('#fullList').dataTable();
	
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
	
	$dTable.getNext = function() {
		var id = $dTable.idInput.value;
		var actualTr = $dTable.getTrNodeContainsId(id);
		var newTr = $dTable.fnGetAdjacentTr( actualTr);
		var data = $dTable.fnGetData(newTr);
		$dTable.insertData(data);
	}
	
	$dTable.getPrevious = function() {
		var id = $dTable.idInput.value;
		var actualTr = $dTable.getTrNodeContainsId(id);
		var newTr = $dTable.fnGetAdjacentTr( actualTr,false);
		var data = $dTable.fnGetData(newTr);
		$dTable.insertData(data);
	}
	
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
         var tr = $dTable.getTrNodeContainsId(id);
         if (tr) {
             tr.setAttribute('class', 'scratched');
         }
     }
	 
	 $dTable.insertRow =  function(id) {   	
		
             var position = $dTable.getPosition(id);
             var data = $dTable.getDataArray(id);
             if (position >= 0 && position < $dTable.tableSize) {
                 $dTable.fnUpdate(data, position);
                 var tr = $dTable.getTrNodeContainsId(id);
                 if (tr) {
                     $(tr).removeClass('scratched');
                 }
             } else {
                 var tr = $dTable.fnAddData(data);
                 $dTable.tableSize++;
                 $dTable.refreshClickRow();
             }
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


	
	
