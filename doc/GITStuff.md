# GIT

Location: `https://github.com:play4science/gwap.git`

## Layout
*   `master`:
    Branch that holds the latest development version
*   `production`:
    Branch where some (in fact only very few) changes important for the production servers are located
*   `...`:
    Other branches based on current needs (for example for students or for testing something

## Useful commands
*   `git clone [location]`:
    Retrieve the repository from the server to a new local directory
*   `git fetch`:
    Retrieve latest changes from server repository without changing the working directory
*   `git pull` (=`git fetch`+`git merge`):
    Retrieve and merge latest changes into current branch
*   `git push`:
    Push changes from current branch to server

## Steps for updating production
Add files for the commit (and also files that are already part of the repository).
Before this, make sure you committed everything. 

Then do the following in your local repository:
	
	git checkout master
	git pull
	git checkout production
	git pull
	git merge master
	git push
	git checkout master
	
Then, do the following on the server:
	
	git pull
	
(here you should see that the files are changed)
