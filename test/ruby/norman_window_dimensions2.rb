# norman_window_dimensions2.rb [jruby-embed]
# jsr223.JRubyEngineTest

def norman_window
  return get_area, get_perimeter
end

def get_area
  @x * @y + Math::PI / 8.0 * @x ** 2.0
end

def get_perimeter
  @x + 2.0 * @y + Math::PI / 2.0 * @x
end

norman_window