using System;
	public int size;		//Size of data in memory. Only really used for arrays
			//Console.WriteLine(topScope.nextAdr);
			obj.adr = topScope.nextAdr++;
			topScope.nextAdr += size -1;
		}	
		if (kind == array) {
			obj.adr = topScope.nextAdr++;
			Console.WriteLine("Array" + size);
			topScope.nextAdr += size -1; //Reserve space for the array elements
		}
		//Console.WriteLine("Object stored at: " +name + " : "+ obj.adr);

	public Obj NewObj (string name, int kind, int type){
           return NewObj (name,  kind,  type, 1);
	}

	