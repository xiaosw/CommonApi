1、c
cmd="clear"

2、ga
dir="."
if [[ -n $1 ]]; then
	dir=$1
fi
cmd="git add $dir"

3、gba
cmd="git branch -a"

4、gcm
cmd="git commit"
if [[ -n $1 ]]; then
	cmd="git commit -m $1"
fi

5、gcma
cmd="git commit --amend"

6、gco
dir="."
if [[ -n $1 ]]; then
	dir=$1
fi
cmd="git checkout $dir"

7、gcp
file_name="."
if [[ -n $1 ]]; then
	file_name=$1
fi
cmd="git cherry-pick $file_name"

8、gdot
command="git push origin :refs/tags/$1"

9、gi
cmd="git init"

10、gpr
cmd="git pull --rebase"

11、gpush
command="git push origin $1"

12、gs
cmd="git stash"
if [[ -n $ ]]; then
	cmd="git stash -m $1"
fi

13、gsl
cmd="git stash list"

14、gsp
num=0
if [[ -n "$1" ]]; then
	num=$1
fi

cmd="git stash pop stash@{$num}"

15、gstatus
cmd="git status"

16、gtags
cmd="git tag"
