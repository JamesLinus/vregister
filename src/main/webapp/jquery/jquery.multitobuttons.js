
/* Convert mulitselect to selectable span buttons and map selected buttons to selected in multiselect
 * @author: Mikołaj Sochacki  mikolajsochacki  at gmail com
 * Licence: MIT
 * */

     jQuery.fn.multiToButtons = function(options) {
    	
    	var plugin = this;
    	var options = options;
    	
        plugin.settings = {
            activeClass: 'selectedTag',
			inactiveClass: 'unselectedTag',			
			maxSelection: 3,
        }
        plugin.working = false;
           
        plugin.selectedValue = new Array();
        plugin.multiSelect = plugin.children('select').first();

        plugin.init = function() {
        	
            if(options != undefined){
            	  if( options.activeClass != undefined) plugin.settings.activeClass = options.activeClass;
                  if( options.inactiveClass != undefined) plugin.settings.inactiveClass = options.inactiveClass;
                  if( options.maxSelection != undefined) plugin.settings.maxSelection = options.maxSelection;
            }
            		      
			 plugin.multiSelect.hide();
			 
		      plugin.multiSelect.children('option').each(function(){
		    	  var tab = document.createElement('span');
		    	  var option = $(this);
		    	  var value = option.val();
		    	  var addClass = "";
		    	  
		    	  if(option.attr('selected') != undefined && option.attr('selected') == 'selected') {
		    		  addClass = plugin.settings.activeClass;
		    		  plugin.selectedValue.push(value);
		    	  }
		    	  else addClass = plugin.settings.inactiveClass;
		    	  
		    	  $(tab).html(value).addClass(addClass);
		    	  plugin.append(tab);
		      });
		      plugin.children('span').each(function(){
		    	  $(this).click(function(){	    		 
		    		  var $span = $(this);
		    		  var tag = $span.html();
		    		  if($span.hasClass('selectedTag'))	{
		  				 $span.removeClass('selectedTag').addClass('unselectedTag');
		  				 var i = plugin.selectedValue.indexOf(tag);
		  				 if(i > -1) plugin.selectedValue.splice(i,1);
		  				 plugin.unmarkSelected(tag);
		  				}
		  				else {
		  					if(plugin.selectedValue.length < plugin.settings.maxSelection){
		  						plugin.selectedValue.push(tag);
		  						$span.removeClass('unselectedTag').addClass('selectedTag');
		  						plugin.markSelected(tag);
		  					}
		  				}
		    	  });
		      });	      	
		      
        }
        
        plugin.markSelected = function(tag){
        	plugin.multiSelect.children('option').each(function(){
        		if(this.value == tag){
        			//alert("Found tag " + tag + " and add selected");
        			$(this).attr('selected', "selected");
        		}
        	});
        }
        
        plugin.unmarkSelected = function(tag){
        	plugin.multiSelect.children('option').each(function(){
        		if(this.value == tag){
        			//alert("Found tag " + tag + " and remove selected");
        			$(this).removeAttr('selected');
        		}
        	});
        }

        plugin.init();

    }
