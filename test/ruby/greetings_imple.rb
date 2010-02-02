# greetings_imple.rb [jruby-emed]
# GetInstanceTest, ScriptingContainerTest

class GreetingsImple
  include Java::org.jruby.embed.Greetings
  def hello(to)
    print "<p>What's up? ", to, "</p>\n"
  end
  def bye(to)
    print "See you, ", to, "\n"
  end
end
GreetingsImple.new
