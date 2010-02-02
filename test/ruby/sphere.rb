# sphere.rb [jruby-embed]
# jsr223.JRubyEngineTest

def volume(r)
  4.0 / 3.0 * Math::PI * r ** 3.0
end

def surface_area(r)
  4.0 * Math::PI * r ** 2.0
end
