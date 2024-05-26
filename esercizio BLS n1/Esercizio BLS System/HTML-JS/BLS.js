var status = 0;

function AccendiSpegniLed()
{	
	if (status == 0)
	{
		status = 1;
		lampeggia();
	}
	else
	{
		document.MyForm.led.style.backgroundColor = "maroon";
		status = 0;
	}
}

function lampeggia()
{
	if (status == 1)
	{
		document.MyForm.led.style.backgroundColor = "red";
		status = 2;
		lampeggia();
	}
	
	if (status == 2)
	{
		document.MyForm.led.style.backgroundColor = "orange";
		status = 1;
		lampeggia();
	}
}
