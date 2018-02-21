/* 
 * This JavaScript file provides methods for clearing the
 * canvas and for rendering Red Box Man.
 */

var canvas;
var gc;
var canvasWidth;
var canvasHeight;
var imageLoaded;
var mousePositionRendering;
var redBoxManImage;
var mouseX;
var mouseY;
var imagesRedBoxManLocations;
var shapesRedBoxManLocations;

function Location(initX, initY) {
    this.x = initX;
    this.y = initY;
}

function init() {
    // GET THE CANVAS SO WE CAN USE IT WHEN WE LIKE
    canvas = document.getElementById("red_box_man_canvas");
    gc = canvas.getContext("2d");
        
    // MAKE SURE THE CANVAS DIMENSIONS ARE 1:1 FOR RENDERING
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;
    
        // FOR RENDERING TEXT
    gc.font="32pt Arial";

    // LOAD THE RED BOX MAN IMAGE SO WE CAN RENDER
    // IT TO THE CANVAS WHENEVER WE LIKE
    redBoxManImage = new Image();
    redBoxManImage.onload = function() {
	imageLoaded = true;
    }
    redBoxManImage.src = "./images/RedBoxMan.png";
    
    // BY DEFAULT WE'LL START WITH MOUSE POSITION RENDERING ON
    mousePositionRendering = true;
    
    // HERE'S WHERE WE'LL PUT OUR RENDERING COORDINATES
    imagesRedBoxManLocations = new Array();
    shapesRedBoxManLocations = new Array();
}

function processMouseClick(event) {
    updateMousePosition(event);
    var location = new Location(mouseX, mouseY);
    if (event.shiftKey) {
	shapesRedBoxManLocations.push(location);
	render();
    }
    else if (event.ctrlKey) {
	if (imageLoaded) {
	    imagesRedBoxManLocations.push(location);
	    render();
	}
    }
    else {
	clear();
    }
}

function clearCanvas() {
    gc.clearRect(0, 0, canvasWidth, canvasHeight);
}

function clear() {
    shapesRedBoxManLocations = [];
    imagesRedBoxManLocations = [];
    clearCanvas();
}

function updateMousePosition(event) {
    var rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;
    render();
}

function renderShapesRedRoundMan(location){
 //Draw Body
	   var bodyX = location.x + (25);
	   var bodyY = location.y + (70);
	   var bodyW = 60;
	   var bodyH = 30;
	   gc.fillStyle = "#000000";
	   gc.fillRect(bodyX, bodyY, bodyW, bodyH);
	   gc.fillRect(bodyX + 7.5, bodyY + 20, 45, 20);
	   gc.fillRect(bodyX - 2, bodyY + 35, 10, 10);
	   gc.fillRect(bodyX + bodyW - 8, bodyY +35, 10, 10);
	   gc.beginPath();

	    var headColor = "#40E0D0";
	    var outlineColor = "#000000";
	    var headW = 115;
	    var headH = 88;

	    //Draw his red head
	    gc.fillStyle = headColor;
	    gc.fillRect(location.x, location.y, headW, headH);
	    gc.beginPath();
	    gc.strokeStyle = outlineColor;
	    gc.lineWidth = 1;
	    gc.rect(location.x, location.y, headW, headH);
	    gc.stroke();

	    //Draw his eyes
	    var eyeColor = "#FFFF00";
	    var eyeW = 30;
	    var eyeH = 20;

	    var leftX = location.x + (15);
	    var leftY = location.y + (25);
	    var rightX = location.x + (65);
	    var rightY= leftY;

	    gc.fillStyle = eyeColor;
	    gc.fillRect(leftX, leftY, eyeW, eyeH);
	    gc.fillRect(rightX, rightY, eyeW, eyeH);
	    gc.beginPath();
	    gc.rect(leftX, leftY, eyeW, eyeH);
	    gc.rect(rightX, rightY, eyeW, eyeH);
	    gc.stroke();

	    //Draw pupils
	    var black = "#000000";
	    var pupilW = 10;
	    var pupilH = 5;
	    var pupilLeftX = leftX + 9;
	    var pupilLeftY = leftY + 8;
	    var pupilRightX = rightX + 9;
	    var pupilRightY = pupilLeftY;

	    gc.fillStyle = black;
	    gc.fillRect(pupilLeftX, pupilLeftY, pupilW, pupilH);
	    gc.fillRect(pupilRightX, pupilRightY, pupilW, pupilH);
	    gc.beginPath();


	   //Draw mouth
	   var mouthX = location.x + 20;
	   var mouthY = location.y + 60;
	   var mouthW = 70;
	   var mouthH = 7;
	   gc.fillRect(mouthX, mouthY, mouthW, mouthH);
	   gc.beginPath();
}

function renderShapesRedBoxMan(location) {
    var headColor = "#DD0000";
    var outlineColor = "#000000";
    var headW = 115;
    var headH = 88;
    
    // DRAW HIS RED HEAD
    gc.fillStyle = headColor;
    gc.fillRect(location.x, location.y, headW, headH);
    gc.beginPath();
    gc.strokeStyle = outlineColor;
    gc.lineWidth = 1;
    gc.rect(location.x, location.y, headW, headH);
    gc.stroke();
    
    // AND THEN DRAW THE REST OF HIM
}

function renderImageRedBoxMan(location) {
    gc.drawImage(redBoxManImage, location.x, location.y);
}

function renderMousePositionInCanvas(event) {
    if (mousePositionRendering) {
	gc.strokeText("(" + mouseX + "," + mouseY + ")", 10, 50);    
    }
}

function toggleMousePositionRendering() {
    mousePositionRendering = !mousePositionRendering;
}

function render() {
    clearCanvas();
    for (var i = 0; i < shapesRedBoxManLocations.length; i++) {
	var location = shapesRedBoxManLocations[i];
	renderShapesRedRoundMan(location);
	//renderShapesRedBoxMan(location);
    }
    for (var j = 0; j < imagesRedBoxManLocations.length; j++) {
	var location = imagesRedBoxManLocations[j];
	renderImageRedBoxMan(location);
    }
    renderMousePositionInCanvas();
}