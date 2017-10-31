var labelType, useGradients, nativeTextSupport, animate;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);
})();
//SUV: This is for upper txt information. 
var Log = {
  elem: false,
  write: function(text){
    if (!this.elem) 
      this.elem = document.getElementById('log');
    this.elem.innerHTML = text;
    this.elem.style.left = (800 - this.elem.offsetWidth / 2) + 'px';
  }
};


function init(){
    //init data
    var json = {
        id: "node100",
        name: "Customer",
        data: {},
        children: [{
            id: "node10",
            name: "3663 ALBA LIMITED",
            data: {level:0},
            children: [{
				id: "node11",
				name: "0000006994 Total National",
				data: {level:1},
				children: [{
					id: "node12",
					name: "0000008714 EL2 - OTHERS",
					data: {level:2},
					children: [{
						id: "node13",
						name: "0910003060 L3:UK 3663 (Bidvest)",
						data: {level:3},
						children: [{
							id: "node14",
							name: "0910004060 L4:UK 3663 (Bidvest)",
							data: {level:4},
							children: [{
								id: "node15",
								name: "0910005060 L5:UK 3663",
								data: {level:5},
								children: [{
									id: "node16",
									name: "0910006060 L6:UK 3663",
									data: {level:6},
									children: [{
										id: "node17",
										name: "0910007060 L7:UK 3663",
										data: {level:7},
										children: []
									}]
								}]
							}]
						}]
					}]
				}]
			}]
		},{
            id: "node20",
            name: "3G FOOD SERVICE LIMITED",
            data: {level:0},
            children: [{
				id: "node21",
				name: "0000006994 Total National",
				data: {level:1},
				children: [{
					id: "node22",
					name: "0000008714 EL2 - OTHERS",
					data: {level:2},
					children: [{
						id: "node23",
						name: "0910003061 L3:UK Other Foodservice",
						data: {level:3},
						children: [{
							id: "node24",
							name: "0910004070 L4:UK Other Distributives",
							data: {level:4},
							children: [{
								id: "node25",
								name: "0910005076 L5:UK FS Frozen Wholesalers",
								data: {level:5},
								children: [{
									id: "node26",
									name: "0910006076 L6:UK FS Frozen Wholesalers",
									data: {level:6},
									children: [{
										id: "node27",
										name: "0910007076 L7:UK FS Frozen Wholesalers",
										data: {level:7},
										children: [{
											id: "node28",
											name: "0000294560 3G FOOD SERVICE LIMITED",
											data: {level:8},
											children:[]
										}]
									}]
								}]
							}]
						}]
					}]
				}]
			}]
		},{
            id: "node30",
            name: "A BARRANCE & CO",
            data: {level:0},
            children: [{
				id: "node31",
				name: "0000006994 Total National",
				data: {level:1},
				children: [{
					id: "node32",
					name: "0000008714 EL2 - OTHERS",
					data: {level:2},
					children: [{
						id: "node33",
						name: "0910003060 L3:UK 3663 (Bidvest)",
						data: {level:3},
						children: [{
							id: "node34",
							name: "0910004060 L4:UK 3663 (Bidvest)",
							data: {level:4},
							children: [{
								id: "node35",
								name: "0910005060 L5:UK 3663",
								data: {level:5},
								children: [{
									id: "node36",
									name: "0910006060 L6:UK 3663",
									data: {level:6},
									children: [{
										id: "node37",
										name: "0910007060 L7:UK 3663",
										data: {level:7},
										children: []
									}]
								}]
							}]
						}]
					}]
				}]
			}]
		}]
    };
    //end
    //init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        //id of viz container element
        injectInto: 'infovis',
        //set duration for the animation
        duration: 800,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 50,
		levelsToShow: 9,
        //enable panning
        Navigation: {
          enable:true,
          panning:true
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            height: 40,
            width: 90,
            type: 'rectangle',
            color: '#aaa',
            overridable: true
        },
        
        Edge: {
            type: 'bezier',
            overridable: true
        },
        
        onBeforeCompute: function(node){
            Log.write("loading " + node.name);
        },
        
        onAfterCompute: function(){
            Log.write("done");
        },
        
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){//SUV: node label style. 
            label.id = node.id;            
            label.innerHTML = node.name;
            label.onclick = function(){
            	//if(normal.checked) {
            	st.onClick(node.id);
            	//} else {
                st.setRoot(node.id, 'animate');
            	//}
            };
            //set label styles
            var style = label.style;
            style.width = 90 + 'px';
            style.height = 40 + 'px';            
            style.cursor = 'pointer';
            style.color = '#333';
            style.fontSize = '0.8em';
            style.textAlign= 'center';
            style.paddingTop = '3px';
        },
        
        //This method is called right before plotting
        //a node. It's usefu  l for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                node.data.$color = "#ff7";
            }
            else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if(!node.anySubnode("exist")) {
                    //count children number
                    var count = 0;
                    node.eachSubnode(function(n) { count++; });
                    //assign a node color based on
                    //how many children it has
                    node.data.$color = ['#aaa', '#baa', '#caa', '#daa', '#eaa', '#faa'][count];                    
                }
            }
        },
        
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#eed";
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    //load json data
    st.loadJSON(json);
    //compute node positions and layout
    st.compute();
    //optional: make a translation of the tree
    st.geom.translate(new $jit.Complex(-200, 0), "current");
    //emulate a click on the root node.
    st.onClick(st.root);
    //end
    //Add event handlers to switch spacetree orientation.
    var top = $jit.id('r-top'), 
        left = $jit.id('r-left'), 
        bottom = $jit.id('r-bottom'), 
        right = $jit.id('r-right'),
        normal = $jit.id('s-normal');
        
    
    function changeHandler() {
        if(this.checked) {
            top.disabled = bottom.disabled = right.disabled = left.disabled = true;
            st.switchPosition(this.value, "animate", {
                onComplete: function(){
                    top.disabled = bottom.disabled = right.disabled = left.disabled = false;
                }
            });
        }
    };
    
    top.onchange = left.onchange = bottom.onchange = right.onchange = changeHandler;
    //end

}
