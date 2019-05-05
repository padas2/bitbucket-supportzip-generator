# bitbucket-supportzip-generator
A Java Based implementation for generating Bitbucket support zip

### Need for creating this specific implementation
When I created my first bitbucket plugin and after I uploaded it to one of our dev instances, I wanted to access bitbucket logs.
And that was difficult as I had to login to a Bastion Host machine first and then login to the bitbucket instance.

After googling ways to automate this, I found that Bitbucket exposed a set of REST End points which could be used to be 
trigger support zip creation(which will contain all the logs we need). This repository will make use of those end points and automate support zip processing for us.

This java based implementation will trigger the REST endpoint to create support zip, wait for its completion, download it 
and unzip it and provide the final destination of the unzipped directory.
