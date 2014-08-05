var citys = {
        "Beijing": {
                    "centerX": 300,
                                "centerY": 160
                                    },
                                            "Tokyo": {
                                                        "centerX": 600,
                                                                    "centerY": 300
                                                                        }
};

var bubbles = new Array();

function drawBubble(canvas, centerX, centerY, radius, color) {
        var context = canvas.getContext("2d");
            context.beginPath();
                context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
                    context.fillStyle = color;
                        context.fill();
}

function drawHotSearchs(canvas) {
        // var time = 5;
            // $.getJSON("http://localhost:9200/_plugin/hotsearch/_allhotsearch?time=" + time, function(data)..
                $.getJSON("http://localhost:9200", function (data) {
                            var objstr = '[\n  {\n    \"Citys\": {\n      \"Tokyo\": {\n        \"Population\": 0.98,\n        \"hot_words:\": {\n          \"money\": 0.45,\n          \"book\": 0.36,\n          \"time\": 0.24\n        }\n      },\n      \"Beijing\": {\n        \"Population\": 0.87,\n        \"hot_words\": {\n          \"house\": 0.45,\n          \"goverment\": 0.33,\n          \"metro\": 0.25\n        }\n      }\n    }\n  },\n  {\n    \"status\": \"ok\",\n    \"content\": {\n      \"location\": \"Tokyo\",\n      \"query\": \"find movie\"\n    }\n  },\n  {\n    \"status\": \"wrong\"\n  }\n]';
                                    var obj = JSON.parse(objstr);

                                            $.each(obj[0].Citys, function (key, item) 
                                                    {
                                                                    bubbles.push({x: citys[key].centerX, y: citys[key].centerY, radius: item.Population * 100, 'rgba(0, 255, 0, 0.6)'});
                                                                                drawBubble(canvas, citys[key].centerX, citys[key].centerY, item.Population * 100, 'rgba(0, 255, 0, 0.6)');
                                                                                        });

                                                    // Event binding for the created bubbles.
                                                            bindBubblesEvent(canvas);
                                                                });
}

function getMousePos(canvas, evt) {
        var rect = canvas.getBoundingClientRect();
            return {
                        x: evt.clientX - rect.left,
                                y: evt.clientY - rect.top
                                    };
}

function bindBubblesEvent(canvas) 
{
        canvas.addEventListener('mousemove', function (evt) {
                    var mousePos = getMousePos(canvas, evt);
                            var message = 'Mouse position: ' + mousePos.x + ',' + mousePos.y;
                                }, false);
}

/*
function drawMap(canvas) 
{
        var context = canvas.getContext("2d");
            var map = new Image();
                map.onload = function () {
                            context.drawImage(map, 0, 0);

                                    // Draw buddles according to hotsearch data.
                                            drawHotSearchs(canvas);
                                                };
                                                    map.src = "img/map.png";//'http://img2.wikia.nocookie.net/__cb20060929175749/lyricwiki/images/4/44/World_Map_(Simple).png';
}
*/

$(function () {
        var canvas = document.getElementById("mapCanvas");

            drawHotSearchs(canvas);

                //context.clearRect(0, 0, canvas.width, canvas.height);
});
