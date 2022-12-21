nmea_sentence = '$GPRMC,194509.000,A,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77'
nmea_sentence_void = '$GPRMC,194509.000,V,random_text'
nmea_sentence_combo = '$GPRMC,194509.000,V,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77$GPRMC,194509.000,V,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77$GPRMC,194509.000,A,4060.6142,N,01400.4168,W,2.03,221.11,160412,,,A*77'

def index_of(s, c, first_or_last=True):
	""" 
	Provide first (T) or last (F) index of given sequence in string

	>>> index_of("this is a doctest", "doctest") 
	10
	>>> index_of("this is a doctest", "no such str") 
	None
	"""
	try:
		if first_or_last:
			return s.index(c)
		return s.index(c) + (len(c)-1)
	except ValueError as e:
		return None

class NMEA_List(list):
	"""
	An object representing multiple NMEA_sequences (with potentially off-frequency input). Append method auto-parses for GPS cords.

	>>> lst = NMEA_List('$GPRMC,194509.000,A,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77')
	>>> lst.cords_list
	[('4042.6142', 'N', ',07400.4168', 'W')]
	
	>>> lst.append('$GPRMC,194509.000,V,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77$GPRMC,194509.000,V,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77
	$GPRMC,194509.000,A,4060.6142,N,01400.4168,W,2.03,221.11,160412,,,A*77')
	>>> lst.cords_list
	[('4042.6142', 'N', ',07400.4168', 'W'), ('4060.6142', 'N', ',01400.4168', 'W')]

	>>> lst.append('$GPRMC,194509.000,A,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77') # same sequence as first case
	>>> lst.cords_list
	[('4060.6142', 'N', ',01400.4168', 'W'), ('4042.6142', 'N', ',07400.4168', 'W')] # order swapped
	"""

	def __init__(self, opening_nmea_seq):
		super().__init__([opening_nmea_seq])
		self.cords_list = []
		self.parse()

	def append(self, nmea_seq):
		if nmea_seq:
			super().append(nmea_seq)
			self.parse()

	def parse(self):
		"""
		Provides (lat, lat_direction, long, long_direction) if active GPS fix found

		>>> parse('$GPRMC,194509.000,A,4042.6142,N,07400.4168,W,2.03,221.11,160412,,,A*77')
		('4042.6142', 'N', ',07400.4168', 'W')
		"""
		p = self[-1]

		GPRMC_ind, Active_ind = index_of(p, 'GPRMC'), index_of(p, ',A,', False)

		# keep parsing till fixed, return most *recent* active
		while p and GPRMC_ind and not Active_ind: #redundancy to account for if refresh rate gets messed up
			res = self.parse(p[:-70])
			if res in self.cords_list:
				self.cords_list.remove(res)
			self.cords_list.append(res)
			

		if GPRMC_ind and Active_ind:
			nmea_sentence = p[GPRMC_ind:]
			lat, lat_dir = nmea_sentence[Active_ind:Active_ind+9], nmea_sentence[Active_ind+10]
			lon, lon_dir = nmea_sentence[Active_ind+11:Active_ind+22], nmea_sentence[Active_ind+23]

			res = (lat, lat_dir, lon, lon_dir)

			if res in self.cords_list: # if cords are in lst, update to show as most recent
				self.cords_list.remove(res)
			self.cords_list.append(res)


	