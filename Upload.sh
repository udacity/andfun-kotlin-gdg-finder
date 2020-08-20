#!/bin/bash

branches=()
eval "$(git for-each-ref --shell --format='branches+=(%(refname:lstrip=2))' refs/heads/)"

for branch in "${branches[@]}"; do
  if [[ "${branch}" != "master" ]]; then
    echo $'\n'"*************************************************************"
    echo "${branch}"
    git checkout ${branch}
    git checkout master -- build.gradle
    git checkout master app/build.gradle
    git checkout master gradle/wrapper/gradle-wrapper.properties
    git add -u
    git commit -m "Update Gradle scripts"
    git push
  fi
done
git checkout master