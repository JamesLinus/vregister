function runPineBoxEqualizer() {
	var $column1 = $('#column1');
	var $column2 = $('#column2');
	var $column3 = $('#column3');
	var nr = 0;
	while (forRunner($column1, $column2, $column3)) {
		nr++;
		if (nr > 12)
			break;
	}
}

function forRunner($column1, $column2, $column3) {
	var sizeCol1 = $column1.height();
	var sizeCol2 = $column2.height();
	var sizeCol3 = $column3.height();

	var $longest = null
	var $shortest = null;
	
	if (sizeCol1 > sizeCol2 && sizeCol1 > sizeCol3) {
		$longest = $column1;
		if (sizeCol2 > sizeCol3)
			$shortest = $column3;
		else
			$shortest = $columnt2;
	} else if (sizeCol2 > sizeCol1 && sizeCol2 > sizeCol3) {
		$longest = $column2;
		if (sizeCol1 > sizeCol3)
			$shortest = $column3;
		else
			$shortest = $columnt1;
	} else {
		$logest = $columnt3;
		if (sizeCol1 > sizeCol2)
			$shortest = $column2;
		else
			$shortest = $columnt1;
	}

	if ($longest.height() > $shortest.height()
			+ $longest.children('div').last().height() + 10) {
		$shortest.append($longest.children('div').last());
		return true;
	} else
		return false;
}