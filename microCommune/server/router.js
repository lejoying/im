function route(handle,pathname,request,response){
    console.log(pathname);

    if(typeof handle[pathname] === "function"){
        handle[pathname](request,response);
    }   else{
        console.log("No request handler found for "+pathname);
        response.writeHead(200,{"Content-Type":"text/plain"});
        response.write("404 not found");
        response.end();
    }
}

exports.route = route;