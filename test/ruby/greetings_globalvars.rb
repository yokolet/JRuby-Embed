# greetings_globalvars.rb [jruby-embed]
# ScriptingContainerTest

def greet
  message = "How are you? #{$who}."
end

def sayhi
  $, = ","
  $\ = "\n"
  print "Hi", $people
  $, = ""
  $\ = nil
end

def count
  $people.size + 1
end