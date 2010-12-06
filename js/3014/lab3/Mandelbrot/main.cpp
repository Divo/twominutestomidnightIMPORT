/*
 * 3BA26 Mandelbrot Project
 * 
 * Using techniques we've covered in class, accelerate the rendering of
 * the M set.
 * 
 * Hints
 * 
 * 1) Vectorize
 * 2) Use threads
 * 3) Load Balance
 * 4) Profile and Optimise
 * 
 * Potential FAQ.
 * 
 * Q1) Why when I zoom in far while palying with the code, why does the image begin to render all blocky?
 * A1) In order to render at increasing depths we must use increasingly higher precision floats
 * 	   We quickly run out of precision with 32 bits floats. Change all floats to doubles if you want
 * 	   dive deeper. Eventually you will however run out of precision again and need to integrate an
 * 	   infinite precision math library or use other techniques.
 * 
 * Q2) Why do some frames render much faster than others?
 * A2) Frames with a lot of black, i.e, frames showing a lot of set M, show pixels that ran until the 
 *     maximum number of iterations was reached before bailout. This means more CPU time was consumed
 */



#include <iostream>
#include <cmath>
#include <sys/time.h>
#include <xmmintrin.h>
#include <string>


#include "Screen.h"

/*
 * You can't change these values to accelerate the rendering.
 * Feel free to play with them to render different images though.
 */
const int 	MAX_ITS = 1000;			//Max Iterations before we assume the point will not escape
const int 	HXRES = 700; 			// horizontal resolution	
const int 	HYRES = 700;			// vertical resolution
const int 	MAX_DEPTH = 15;		// max depth of zoom    SET BACK TO 480
const float ZOOM_FACTOR = 2.02;		// zoom between each frame

/* Change these to zoom into different parts of the image */
const float PX = -0.702295281061;	// Centre point we'll zoom on - Real component
const float PY = +0.350220783400;	// Imaginary component

/*
 * The palette. Modifying this can produce some really interesting renders.
 * The colours are arranged R1,G1,B1, R2, G2, B2, R3.... etc.
 * RGB values are 0 to 255 with 0 being darkest and 255 brightest
 * 0,0,0 is black
 * 255,255,255 is white
 * 255,0,0 is bright red
 */
unsigned char pal[]={
	255,180,4,
	240,156,4,
	220,124,4,
	156,71,4,
	72,20,4,
	251,180,4,
	180,74,4,
	180,70,4,
	164,91,4,
	100,28,4,
	191,82,4,
	47,5,4,
	138,39,4,
	81,27,4,
	192,89,4,
	61,27,4,
	216,148,4,
	71,14,4,
	142,48,4,
	196,102,4,
	58,9,4,
	132,45,4,
	95,15,4,
	92,21,4,
	166,59,4,
	244,178,4,
	194,121,4,
	120,41,4,
	53,14,4,
	80,15,4,
	23,3,4,
	249,204,4,
	97,25,4,
	124,30,4,
	151,57,4,
	104,36,4,
	239,171,4,
	131,57,4,
	111,23,4,
	4,2,4};
const int PAL_SIZE = 40;  //Number of entries in the palette 

//Modify the pallet to produce boring but clear results
/* 
 * Return true if the point cx,cy is a member of set M.
 * iterations is set to the number of iterations until escape.
 */
bool member(float cx, float cy, int& iterations)
{
	float x = 0.0;
	float y = 0.0;
	iterations = 0;
	while ((x*x + y*y < (2*2)) && (iterations < MAX_ITS)) {
		float xtemp = x*x - y*y + cx;
		y = 2*x*y + cy;
		x = xtemp;
		iterations++;
	}

	return (iterations == MAX_ITS);

}

void out_m128(__m128 x){
   float * temp = (float *)malloc(sizeof(float) * 4);
   _mm_storeu_ps(temp, x);
   int i;
   for(i = 0; i < 4; i++){
      std::cout << temp[i] << " ";
   }
   std::cout << std::endl;
}


//Determine membership of the set.
/*IMPORTANT, because SSE has different precision characteristics to plain ol' floats (supposidly)
  the number of iterations returned may be out by 1 */
__m128 member128(__m128 cx, __m128 cy)
{
   __m128 x = _mm_set1_ps(0.0);
   __m128 y = _mm_set1_ps(0.0); //y = x?
   __m128 iterations = _mm_set1_ps(0.0);
   __m128 iterations_temp = _mm_set1_ps(0.0);
   __m128 four = _mm_set1_ps(4.0);
   __m128 two = _mm_set1_ps(2.0);
   __m128 mask = _mm_set1_ps(1.0);
   __m128 temp;
   int iteration_cheat = 0;
   //This is gonna look nasty, maybe try operator overloading later
   //While at least one value is < 4
   //Quicker to have an int keeping track of loop iterations or keep using SSE???
   while( (_mm_movemask_ps(iterations_temp = 
            (_mm_cmplt_ps( _mm_add_ps( _mm_mul_ps(x,x),_mm_mul_ps(y,y)), four )))) != 0
           && iteration_cheat < MAX_ITS){
      iterations = _mm_add_ps(iterations, _mm_and_ps(iterations_temp, mask)); //Introducing extra instruction
      //The cmp returns 0 if false and NaN if true (not -1). Don't know what NaN is so AND it with
      //1 and add that to the count.

      temp = _mm_add_ps( _mm_sub_ps( _mm_mul_ps(x,x), _mm_mul_ps(y,y)), cx);
      y = _mm_add_ps( _mm_mul_ps( _mm_mul_ps(x,y), two), cy);
      x = temp;

      /****IMPORTANT NOTE TO SELF*******
        Don't EVER call malloc in a loop like this, especially with threads!
      *************/  
      //float * tempF = (float *) malloc(sizeof(float)*4);

      iteration_cheat++;
      
   }

        
   
   return iterations;
}

/*void test(__m128 a, __m128 b){
   __m128 temp;
   __m128 mask = _mm_set1_ps(1.0);
   std::cout << _mm_movemask_ps(_mm_cmpge_ps(a, b)) << std::endl;
   out_m128((temp = _mm_cmpge_ps(a, b)) );
   out_m128(_mm_and_ps(temp, mask));

}*/

//Map an into from one range to another
float map_range(float value, float in_start, float in_stop, float out_start, float out_stop){
   return (out_start + (out_stop - out_start) * ((value - in_start)/(in_stop - in_start)));
}

int main()
{
	float m=1.0; /* initial  magnification Shared		*/

	/* Create a screen to render to */
#ifdef SCREEN
	Screen *screen; //Shared
	screen = new Screen(HXRES, HYRES); //Shared
   sleep(2);
#endif
   int depth=0;
   int hx,hy;
   struct timeval start_time, stop_time; //unique
   
   long long compute_time; //unique

   while(depth < MAX_DEPTH){
   
      gettimeofday(&start_time,NULL); /*Computes the time of the longest 
                                        thread including thread startup time*/
      #pragma omp parallel private(hx, hy)//, start_time, stop_time, compute_time)
      {

            float * temp = (float *)malloc(sizeof(float) * 4);

            #pragma omp for schedule(dynamic) //This schedule shaved another ~2 microseconds (From 3 down to 1) off in testing. Any small chunk size, ( 1 < X < ~40) 
		for (hy=0; hy<HYRES; hy++) {      //seems to have roughly the same result.
         float cy = (((float)hy/(float)HYRES) -0.5 + (PY/(4.0f/m)))*(4.0f/m);

         __m128 my = _mm_set1_ps(cy);
         float zoom = (4.0f/m);
         /*__m128 my = _mm_set1_ps(hy);
         my = _mm_div_ps(my, _mm_set1_ps(HYRES));
         my = _mm_add_ps(my, _mm_set1_ps(-0.5 + (PY/zoom)));
         my = _mm_mul_ps(my, _mm_set1_ps(zoom));*/
         int iterations;

#ifdef SSE
         for(hx=0; hx<HXRES;hx+=4){ 
         
                        __m128 mx = _mm_setr_ps((float)hx, (float)(hx + 1), (float)(hx + 2), (float)(hx + 3));
            mx = _mm_div_ps(mx, _mm_set1_ps(HXRES));
            mx = _mm_add_ps(mx, _mm_set1_ps(-0.5 + (PX/zoom)));
            mx = _mm_mul_ps(mx, _mm_set1_ps(zoom));


            //Quick way to pull out the 4 values
            __m128 result = member128(mx, my);
            _mm_store_ps(temp, result);
            int i;
            #ifdef SCREEN
            for(i = 0; i < 4; i++){
               if(temp[i] == MAX_ITS){
                  screen->putpixel(hx+i, hy, 0, 0, 0);
               }else{
                  int x = (((int)temp[i])%40) - 1;
                  int b = x*3;
                  screen->putpixel(hx+i, hy, pal[b], pal[b+1], pal[b+2]);
               }  
             }
            #endif
         }
#else
         for(hx=0; hx<HXRES; hx++){
            float cx = ((((float)hx/(float)HXRES) -0.5 + (PX/(4.0f/m)))*(4.0f/m));

		      if (!member(cx, cy, iterations)) {
		      // Point is not a member, colour based on number of iterations before escape 
		         int i=(iterations%40) - 1; //adjust number of iterations for pallet size
		         int b = i*3;
   //           int b = (int)map_range(iterations, 0, (MAX_ITS-1) , 0, 254); //Map the number of iterations 
               //b = b * 3;                                                               //to a position in pal[]
		      #ifdef SCREEN 
			      screen->putpixel(hx, hy, pal[b], pal[b+1], pal[b+2]);
            #endif
		      } else {
		      // Point is a member, colour it black 
            #ifdef SCREEN
			      screen->putpixel(hx, hy, 0, 0, 0);
        	   #endif
		      }
      
	      }
#endif
     	}
		}
      
		/* Show the rendered image on the screen */
      //Stop parallel here
      #ifdef SCREEN
		   screen->flip();
      #endif
      gettimeofday(&stop_time,NULL);
      compute_time = (stop_time.tv_sec - start_time.tv_sec) * 1000000L + 
         (stop_time.tv_usec - start_time.tv_usec);
		std::cout << "Render done " << depth++ << " Zoom: " << m << " in " << compute_time << " microseconds" << std::endl;
		/* Zoom in */
      //char filename[100];
      //sprintf(filename, "Render%din%lld.bmp\0",m,compute_time);
     //screen->Save_Screen(filename);
		m *= ZOOM_FACTOR;
	}
	
	//sleep(10);
	std::cout << "Clean Exit" << std::endl;

}
