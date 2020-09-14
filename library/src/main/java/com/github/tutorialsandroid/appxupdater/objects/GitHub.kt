package com.github.tutorialsandroid.appxupdater.objects


/**
 * @author Tushar Masram
 */
class GitHub(var gitHubUser: String, var gitHubRepo: String) {

  companion object {
    fun isGitHubValid(gitHub: GitHub?): Boolean {
      return !(gitHub == null || gitHub.gitHubUser.isEmpty() || gitHub.gitHubRepo.isEmpty())
    }
  }
}
