//for playing back files recorded with IBVArecorder

IBVAplayer {
	var <ibva, <sr, oldSr, data, task;
	*new {|ibva|
		^super.new.initIBVAplayer(ibva);
	}
	initIBVAplayer {|argIbva|
		ibva= argIbva;
	}
	prepare {|path|
		var dict= IBVAfile.read(path);
		sr= dict.sr;
		data= dict.data;
	}
	play {|rate= 1, repeats= 1, action|
		oldSr= ibva.sr;
		ibva.sr= sr;
		ibva.srAction.value(sr);
		if(data.notNil, {
			ibva.task.stop;  //take over reading from serialport
			task.stop;
			task= Routine({
				var arr= [-1, -1, -1, -1];
				repeats.do{
					data.do{|val, i|
						arr[i%4]= val;
						if(i%4==3, {
							ibva.data= arr;
							ibva.action.value(*arr);
							(1/sr/rate).wait;
						});
					};
				};
				{action.value(this)}.defer;
			}).play(SystemClock);
		}, {
			"IBVAplayer: file not read - .prepare first".warn;
		});
	}
	stop {
		task.stop;
		if(oldSr.notNil, {
			ibva.sr= oldSr;
			ibva.srAction.value(oldSr);
		});
		ibva.task.reset;
		ibva.task.play(SystemClock);  //restart reading from serialport
	}
}