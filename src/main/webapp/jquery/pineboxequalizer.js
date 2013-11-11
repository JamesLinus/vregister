
function runPineBoxEqualizer() {
	var $column1 = $('#column1');
	var $column2 = $('#column2');
	var $column3 = $('#column3');

var nr = 0;
while (forRunner($column1, $column2, $column3)) {
	nr++;
	if (nr > 20)
		break;
}
}


function forRunner($column1, $column2, $column3) 
{
	var sizeCol1 = $column1.height();
	var sizeCol2 = $column2.height();
	var sizeCol3 = $column3.height();
	
	var $longest = null
		if(sizeCol1 > sizeCol2 && sizeCol1 > sizeCol3) $longest = $column1;
		else if(sizeCol2 > sizeCol1 && sizeCol2 > sizeCol3) $longest = $column2;
		else $longest = $column3;
	var $shortest = null;
	if(sizeCol1 < sizeCol2 && sizeCol1 < sizeCol3) $shortest = $column1;
	else if(sizeCol2 < sizeCol1 && sizeCol2 < sizeCol3) $shortest = $column2;
	else $shortest = $column3;
	
	if($longest.height()  > $shortest.height() + $longest.children('div').last().height())
	{
		 $shortest.append($longest.children('div').last());
		 return true;
	}
	else return false;
	
}
