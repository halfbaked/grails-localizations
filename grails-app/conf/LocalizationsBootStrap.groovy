import org.grails.plugins.localization.*

class LocalizationsBootStrap {

  def init = { servletContext ->
    Localization.load()
  }

}
