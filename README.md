# MultipartUtil
This is a java util that helps us to send multipart/form-data

#Setps to use 

1. First initialise the MultipartUtil class with the URL to which we need to send data
2. Then send form values other than files via sendForm() method and if any filesto be send them via sendFormFile() method
3. After sending every form data make sure you called the finish() method so that the server will know where the message is ending
4. Then further if you want to see what is the response of the service use the printResponse() method
