## Documentation for Spoon

* CI/CD, see <https://github.com/INRIA/spoon/blob/master/doc/ci-cd.md>
* Supply-chain, see <https://github.com/INRIA/spoon/blob/master/doc/SUPPLY-CHAIN.md>
 
### Deploy the Website

To deploy an instance of this website, we use a personal script because the structure of this project isn't standard. We can't have markdown files outside the working directory of Jekyll. So:

1. [Configure your jekyll environment](http://jekyllrb.com/docs/installation/).
2. Launch `./_release/deploy_local.sh`.

Finally, if you want to deploy this website in the server of INRIA, you can use the script `./_release/deploy_website.sh`.

Pull requests welcome.
