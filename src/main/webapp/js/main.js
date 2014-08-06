var citys = 
{
    "Tokyo": 
    {
        "x": 1189,
        "y": 201,
        "color": '0, 255, 0, '
    },
    "Sendai": 
    {
        "x": 1188,
        "y": 191,
        "color": '0, 0, 255, '
    },
    "Nagoya": {
        "x": 1184,
        "y": 204,
        "color": '255, 0, 0, '
    },
    "Beijing": {
        "x": 1088,
        "y": 183,
        "color": '255, 255, 0, '
    },
    "Shanghai": {
        "x": 1122,
        "y": 225,
        "color": '0, 255, 255, '
    },
    "Hong Kong": {
        "x": 1102,
        "y": 262,
        "color": '255, 0, 255, '
    },
    "New York": {
        "x": 357,
        "y": 180,
        "color": '64, 168, 0, '
    },
    "Chicago": {
        "x": 310,
        "y": 174,
        "color": '0, 255, 64, '
    },
    "Boston": {
        "x": 375,
        "y": 168,
        "color": '32, 64, 168, '
    },
    "London": {
        "x": 645,
        "y": 130,
        "color": '0, 128, 255, '
    },
    "Paris": {
        "x": 654,
        "y": 145,
        "color": '190, 32, 0, '
    },
    "Berlin": {
        "x": 692,
        "y": 129,
        "color": '228, 0, 64, '
    },
    "Singapore": {
        "x": 1072,
        "y": 356,
        "color": '0, 255, 0, '
    },
    "Moscow": {
        "x": 765,
        "y": 104,
        "color": '0, 255, 0, '
    },
    "New Delhi": {
        "x": 952,
        "y": 231,
        "color": '0, 255, 0, '
    },
    "Seoul": {
        "x": 1136,
        "y": 194,
        "color": '0, 255, 0, '
    },
    "Sydney": {
        "x": 1244,
        "y": 511,
        "color": '0, 255, 0, '
    },
    "Rome": {
        "x": 692,
        "y": 174,
        "color": '43, 255, 23, '
    },
    "Toronto": {
        "x": 346,
        "y": 164,
        "color": '6, 255, 36, '
    },
    "Florence": {
        "x": 320,
        "y": 211,
        "color": '89, 255, 0, '
    }
};

var maxsize = 50;
var lighter = "0.01";
var light = "0.6";
var dense = "1.0";
var bubbles = [];
var timerange = 7;

var focusedBubble = -1;

var citymap = {};

function message(msg)
{
    $("#messages").empty().append($("<span/>").html(msg));
}

function drawBubble(canvas, bubble)
{
    var context = canvas.getContext("2d");
    context.beginPath();
    context.arc(bubble.x, bubble.y, bubble.radius, 0, 2 * Math.PI, false);
    context.fillStyle = "rgba(" + bubble.color + bubble.alpha + ")";
    context.fill();

    context.beginPath();
    context.arc(bubble.x, bubble.y, 2, 0, 2 * Math.PI, false);
    context.fillStyle = "white";
    context.fill();

    context.font      = "normal bold 15px Verdana";
    context.fillStyle = "rgba(" + bubble.tcolor + bubble.talpha + ")";
    var offset = 0;
    for (var word in bubble.words)
    {
        context.fillText(word, bubble.x + bubble.radius / 6, bubble.y - bubble.radius / 4 + offset);
        offset += 20;
    }

    context.font      = "normal bold 10px Verdana";
    context.fillStyle = "red";
    context.fillText(bubble.city, bubble.x - 5, bubble.y + 15);
}

function drawAllBubbles(canvas)
{
    var context = canvas.getContext("2d");
    context.clearRect(0, 0, canvas.width, canvas.height);
    for (var index = 0; index < bubbles.length; index ++)
    {
        drawBubble(canvas, bubbles[index]);
    }
}

function drawHotSearchs(canvas) 
{
    bubbles = [];
    var context = canvas.getContext("2d");
    context.clearRect(0, 0, canvas.width, canvas.height);

    // $.getJSON("http://localhost:9200", function(data) 
    $.getJSON("http://54.178.105.108:49155/plugin_hotsearch/_allhotsearch?time=" + timerange, function(data)
    {
        var obj = data;

        var minv = 1.0;
        var maxv = 0.0;
        $.each(obj.Citys, function(key, item)
        {
            if (item.Population > maxv)
            {
                maxv = item.Population;
            }
            if (item.Population < minv)
            {
                minv = item.Population;
            }
        });

        $.each(obj.Citys, function (key, item) 
        {
            var bubble = {
                            city: key,
                            x: citys[key].x, 
                            y: citys[key].y, 
                            radius: ((item.Population - minv) / (maxv - minv) + 0.2) * maxsize, 
                            color: citys[key].color,
                            tcolor: "0, 0, 0, ",
                            alpha: light,
                            talpha: lighter,
                            words: item.hot_words
                        };
            bubbles.push(bubble);
            citymap.city = bubble;
            drawBubble(canvas, bubble);
        });
    });
}

function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}

function isPtInBubble(point, bubble)
{
    var distance = (bubble.x - point.x) * (bubble.x - point.x) + (bubble.y - point.y) * (bubble.y - point.y);
    return (distance < (bubble.radius * bubble.radius));
}

function bindCanvasEvent(canvas) 
{
    canvas.addEventListener('mousemove', function (evt) 
    {
        var mousePos = getMousePos(canvas, evt);
        var bubbleIndex = -1;
        for (var index = 0; index < bubbles.length; index ++)
        {
            if (isPtInBubble(mousePos, bubbles[index]))
            {
                bubbleIndex = index;
                break;
            }
        }

        if (bubbleIndex != -1)
        {
            if (focusedBubble != bubbleIndex)
            {
                bubbles[bubbleIndex].alpha = dense;
                bubbles[bubbleIndex].talpha = dense;
                drawAllBubbles(canvas);
                focusedBubble = bubbleIndex;
            }
        }
        else
        {
            if (focusedBubble != -1)
            {
                bubbles[focusedBubble].alpha = light;
                bubbles[focusedBubble].talpha = lighter;
                drawAllBubbles(canvas);
                focusedBubble = -1;
            }
        }
    }, false);
}

function sleep(milliseconds) 
{
  var start = new Date().getTime();
  for (var i = 0; i < 1e7; i++) 
  {
    if ((new Date().getTime() - start) > milliseconds)
    {
      break;
    }
  }
}

$(function () 
{
    var canvas = document.getElementById("mapCanvas");

    //drawHotSearchs(canvas);

    $("#timeranger").change(function()
    {
        time = $(this).val();
        drawHotSearchs(canvas);
    });

    $("#timeranger").change();

    // Event binding for the created bubbles.
    bindCanvasEvent(canvas);

    setInterval(function()
    {
        $.getJSON("http://54.178.105.108:49155/plugin_hotsearch/_searchnow", function(data)
        {
            if (typeof(data.content) !== 'undefined')
            {
                var city = data.content.location;
                var info = citys[city];
                var context = canvas.getContext("2d");
                message(info.x);    
                context.beginPath();
                context.arc(info.x, info.y, 50, 0, 2 * Math.PI, false);
                context.fillStyle = "red";
                context.fill();

                sleep(50);

                context.clearRect(citymap.city.x - citymap.city.radius, citymap.city.y - citymap.city.radius, 
                    citymap.city.x + citymap.city.radius, citymap.city.y + citymap.city.radius);
                drawBubble(canvas, citymap.city);
            }
        });

    }, 1000);
});